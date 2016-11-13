package states;

import static handler.B2dVariables.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import entities.Crystal;
import entities.HUD;
import entities.Player;
import entities.Spike;
import handler.B2dVariables;
import handler.Background;
import handler.BoundedCamera;
import handler.GameStateManager;
import handler.MyContactListener;
import handler.MyInput;
import main.Game;

public class Play extends GameState{

	/*
	 * static body - dont move, unaffected by forces
	 * kinematic body - dont get affected by forces, but applyable velocity
	 * dynamic body - always get affected by forces
	 */
	
	private boolean debug = false;
	
	private World world;
	private Box2DDebugRenderer b2dr;
	private MyContactListener cl;
	private BoundedCamera b2dCam;
	
	private TiledMap tileMap;
	private int tileSize;
	private int tileMapWidth;
	private int tileMapHeight;
	private OrthogonalTiledMapRenderer tmr;
	
	private Player player;
	private Array<Crystal> crystals;
	private Array<Spike> spikes;
	
	private HUD hud;
	private Background[] backgrounds;
	
	public static int level;
	
	public Play(GameStateManager gsm){
		super(gsm);
		
		/* set up box2d stuff */
		world = new World(new Vector2(0, -7f), true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();
		
		/* create player */
		createPlayer();
		
		/* create tiles */
		createWalls();
		cam.setBounds(0, tileMapWidth * tileSize, 0, tileMapHeight * tileSize);
		
		/* create crystals */
		createCrystals();
		player.setTotalCrystals(crystals.size);
		
		/* create spikes */
		createSpikes();
		
		/* create backgrounds */
		Texture bgs = Game.res.getTexture("bgs");
		TextureRegion sky = new TextureRegion(bgs, 0, 0, 320, 240);
		TextureRegion clouds = new TextureRegion(bgs, 0, 240, 320, 240);
		TextureRegion mountains = new TextureRegion(bgs, 0, 480, 320, 240);
		backgrounds = new Background[3];
		backgrounds[0] = new Background(sky, cam, 0f);
		backgrounds[1] = new Background(clouds, cam, 0.1f);
		backgrounds[2] = new Background(mountains, cam, 0.2f);
		
		/* set up HUD */
		hud = new HUD(player);

		/* set up box2d camera */
		b2dCam = new BoundedCamera();
		b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
		b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
	
	}
	
	@Override
	public void handleInput() {
		
		/* player jump */
		if(MyInput.isPressed(MyInput.BUTTON1)){
			playerJump();
		}
		if(MyInput.isPressed(MyInput.BUTTON2)){
			switchBlocks();
		}
		
		if(MyInput.isPressed()){
			if(MyInput.x < Gdx.graphics.getWidth() / 2) {
				switchBlocks();
			}
			else {
				playerJump();
			}
		}
		
	}

	@Override
	public void update(float dt) {
		
		handleInput();
		
		world.step(Game.STEP, 1, 1);
		
		/* remove crystals */
		Array<Body> bodies = cl.getBodiesToRemove();
		
		for (int i = 0; i < bodies.size; i++) {
			Body b = bodies.get(i);
			crystals.removeValue((Crystal) b.getUserData(), true);
			world.destroyBody(b);
			player.collectCrystal();
			Game.res.getSound("crystal").play();
		}
		
		bodies.clear();
		
		player.update(dt);
		
		/* check if player wins */
		if(player.getBody().getPosition().x * PPM > tileMapWidth * tileSize){
			Game.res.getSound("levelselect").play();
			gsm.setState(GameStateManager.LEVEL_SELECT);
		}
		
		/* check if player failed */
		if(player.getBody().getPosition().y < 0){
			Game.res.getSound("hit").play();
			gsm.setState(GameStateManager.MENU);
		}
		if(player.getBody().getLinearVelocity().x < 0.001f){
			Game.res.getSound("hit").play();
			gsm.setState(GameStateManager.MENU);
		}
		if(cl.isPlayerDead()){
			Game.res.getSound("hit").play();
			gsm.setState(GameStateManager.MENU);
		}
		
		/* update crystals */
		for (int i = 0; i < crystals.size; i++) {
			crystals.get(i).update(dt);
		}
		
		/* update spikes */
		for (int i = 0; i < spikes.size; i++) {
			spikes.get(i).update(dt);
		}
		
	}

	@Override
	public void render() {

//		/* clear screen */
//		Gdx.gl20.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		/* camera follows player */
		cam.setPosition(player.getPosition().x * PPM + Game.V_WIDTH / 4, Game.V_HEIGHT / 2);
		cam.update();
		
		/* draw background */
		sb.setProjectionMatrix(hudCam.combined);
		for (int i = 0; i < backgrounds.length; i++) {
			backgrounds[i].render(sb);
		}
		
		/* draw tilemap */
		tmr.setView(cam);
		tmr.render();
		
		/* draw player */
		sb.setProjectionMatrix(cam.combined);
		player.render(sb);
		
		/* draw crystals */
		for (int i = 0; i < crystals.size; i++) {
			crystals.get(i).render(sb);
		}
		
		/* draw spikes */
		for (int i = 0; i < spikes.size; i++) {
			spikes.get(i).render(sb);
		}

		/* draw hud */
		sb.setProjectionMatrix(hudCam.combined);
		hud.render(sb);

		/* draw box2d world */
		if(debug){
			b2dCam.setPosition(player.getPosition().x + Game.V_WIDTH / 4 / PPM, Game.V_HEIGHT / 2 / PPM);
			b2dCam.update();
			b2dr.render(world, b2dCam.combined);
		}
	}

	@Override
	public void dispose() {}
	
	public void createPlayer(){
		
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(100 / PPM, 200 / PPM);
		bdef.fixedRotation = true;
		bdef.linearVelocity.set(0.9f,0);
		
		Body body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(13 / PPM, 13 / PPM);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.density = 1;
		fdef.friction = 0;
		fdef.filter.categoryBits = B2dVariables.BIT_PLAYER;
		fdef.filter.maskBits = B2dVariables.BIT_RED_BLOCK | B2dVariables.BIT_CRYSTAL | B2dVariables.BIT_SPIKE;
		
		body.createFixture(fdef).setUserData("player");
		shape.dispose();
		
		shape = new PolygonShape();
		shape.setAsBox(13 / PPM,  2 / PPM, new Vector2(0, -13 / PPM), 0);
		
		fdef.shape = shape;
		fdef.filter.categoryBits = B2dVariables.BIT_PLAYER;
		fdef.filter.maskBits = B2dVariables.BIT_RED_BLOCK;
		fdef.isSensor = true;
		
		body.createFixture(fdef).setUserData("foot");
		shape.dispose();
		
		player = new Player(body);
		
		body.setUserData(player);
		
		MassData md = body.getMassData();
		md.mass = 1;
		body.setMassData(md);
	}

	public void createWalls(){
		
		/* load tile map */
		
		try{
			tileMap = new TmxMapLoader().load("res/maps/level"+level+".tmx");			
		}catch (Exception e){
			System.out.println("Could not load map: res/maps/level" + level +".tmx");
			Gdx.app.exit();
		}
		tmr = new OrthogonalTiledMapRenderer(tileMap);
				
		tileMapWidth = (int)tileMap.getProperties().get("width");
		tileMapHeight = (int)tileMap.getProperties().get("height");
		tileSize = (int)tileMap.getProperties().get("tilewidth");
		
		TiledMapTileLayer layer;
		layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
		createBlocks(layer, B2dVariables.BIT_RED_BLOCK);
		layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
		createBlocks(layer, B2dVariables.BIT_GREEN_BLOCK);
		layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
		createBlocks(layer, B2dVariables.BIT_BLUE_BLOCK);
	}
	
	private void createBlocks(TiledMapTileLayer layer, short bits){
		
		float ts = layer.getTileWidth();
		
		/* go through all cells and layers */
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {
				
				/* get cell */
				Cell cell = layer.getCell(col, row);
				
				if(cell == null){
					continue;
				}
				if(cell.getTile() == null){ 
					continue;
				}
				
				/* create body and fixture from cell */
				BodyDef bdef = new BodyDef();
				bdef.type = BodyType.StaticBody;
				bdef.position.set((col + 0.5f) * ts / PPM, (row + 0.5f) * ts / PPM);
				
				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[3];
				v[0] = new Vector2(-tileSize / 2 / PPM, -tileSize / 2 / PPM);
				v[1] = new Vector2(-tileSize / 2 / PPM, tileSize / 2 / PPM);
				v[2] = new Vector2(tileSize / 2 / PPM, tileSize / 2 / PPM);
				
				cs.createChain(v);
				
				FixtureDef fdef = new FixtureDef();
				fdef.friction = 0;
				fdef.shape = cs;
				fdef.filter.categoryBits = bits;
				fdef.filter.maskBits = B2dVariables.BIT_PLAYER;
				fdef.isSensor = false;
				
				world.createBody(bdef).createFixture(fdef);
				cs.dispose();
				
			}
		}
	}
	
	private void createCrystals(){
		
		crystals = new Array<>();
		
		MapLayer layer = tileMap.getLayers().get("crystals");
		
		if(layer == null){
			return;
		}
		
		for (MapObject mo : layer.getObjects()) {
			
			BodyDef bdef = new BodyDef();
			bdef.type = BodyType.StaticBody;
			float x = (float) mo.getProperties().get("x") / PPM;
			float y = (float) mo.getProperties().get("y") / PPM;
			
			bdef.position.set(x,y);
			Body body = world.createBody(bdef);
			FixtureDef fdef = new FixtureDef();
			
			CircleShape cshape = new CircleShape();
			cshape.setRadius(8 / PPM);
			
			fdef.shape = cshape;
			fdef.isSensor = true;
			fdef.filter.categoryBits = B2dVariables.BIT_CRYSTAL;
			fdef.filter.maskBits = B2dVariables.BIT_PLAYER;
			
			body.createFixture(fdef).setUserData("crystal");
			
			Crystal c = new Crystal(body);
			crystals.add(c);
			
			body.setUserData(c);
			
			cshape.dispose();
			
		}
		
	}
	
	public void createSpikes(){
		
		spikes = new Array<Spike>();
		
		MapLayer ml = tileMap.getLayers().get("spikes");
		if(ml == null){
			return ;
		}
		
		for(MapObject mo : ml.getObjects()){
			
			BodyDef bdef = new BodyDef();
			bdef.type = BodyType.StaticBody;
			float x = (float)mo.getProperties().get("x") / PPM;
			float y = (float)mo.getProperties().get("y") / PPM;
			bdef.position.set(x,y);
			
			Body body = world.createBody(bdef);
			FixtureDef fdef = new FixtureDef();
			CircleShape cshape = new CircleShape();
			cshape.setRadius(5 / PPM);
			fdef.shape = cshape;
			fdef.isSensor = true;
			fdef.filter.categoryBits = B2dVariables.BIT_SPIKE;
			fdef.filter.maskBits = B2dVariables.BIT_PLAYER;
			body.createFixture(fdef).setUserData("spike");
			
			Spike s = new Spike(body);
			body.setUserData(s);
			spikes.add(s);
			cshape.dispose();
			
		}
		
	}
	
	public void playerJump(){
		if(cl.isPlayerOnGround()){
			player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 0);
			player.getBody().applyForceToCenter(0,  200, true);
			Game.res.getSound("jump").play();
		}
	}
	
	private void switchBlocks(){
		
		Filter filter = player.getBody().getFixtureList().first().getFilterData();
		
		short bits = filter.maskBits;
		
		/* switch to next color red -> green -> blue -> red */
		if((bits & B2dVariables.BIT_RED_BLOCK) != 0){
			bits &= ~B2dVariables.BIT_RED_BLOCK;
			bits |= B2dVariables.BIT_GREEN_BLOCK;
		}else if((bits & B2dVariables.BIT_GREEN_BLOCK) != 0){
			bits &= ~B2dVariables.BIT_GREEN_BLOCK;
			bits |= B2dVariables.BIT_BLUE_BLOCK;
		}else if((bits & B2dVariables.BIT_BLUE_BLOCK) != 0){
			bits &= ~B2dVariables.BIT_BLUE_BLOCK;
			bits |= B2dVariables.BIT_RED_BLOCK;
		}
		
		filter.maskBits = bits;
		player.getBody().getFixtureList().get(1).setFilterData(filter);
		
		bits |= B2dVariables.BIT_CRYSTAL | B2dVariables.BIT_SPIKE;
		filter.maskBits = bits;
		player.getBody().getFixtureList().get(0).setFilterData(filter);
		
		Game.res.getSound("changeblock").play();
	}
	
}
