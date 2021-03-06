package main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.GLOnlyTextureData;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import handler.BoundedCamera;
import handler.Content;
import handler.GameStateManager;
import handler.MyInput;
import handler.MyInputProcessor;

public class Game implements ApplicationListener{

	public static final String TITLE = "BLOCK BUNNY";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int SCALE = 2;
	public static final float STEP = 1 / 60f;
	
	private SpriteBatch sb;
	private BoundedCamera cam;
	private OrthographicCamera hudCam;
	
	private GameStateManager gsm;
	
	public static Content res;
	
	public void create() {
		
		Gdx.input.setInputProcessor(new MyInputProcessor());
		
		res = new Content();
		res.loadTexture("res/images/menu.png");
		res.loadTexture("res/images/bgs.png");
		res.loadTexture("res/images/bunny.png");
		res.loadTexture("res/images/crystal.png");
		res.loadTexture("res/images/hud.png");
		res.loadTexture("res/images/spikes.png");
		
		res.loadSound("res/sfx/jump.wav");
		res.loadSound("res/sfx/crystal.wav");
		res.loadSound("res/sfx/levelselect.wav");
		res.loadSound("res/sfx/hit.wav");
		res.loadSound("res/sfx/changeblock.wav");
		
		res.loadMusic("res/music/bbsong.ogg");
		res.getMusic("bbsong").setLooping(true);
		res.getMusic("bbsong").setVolume(0.5f);
		res.getMusic("bbsong").play();
		
		cam = new BoundedCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		
		sb = new SpriteBatch();
		gsm = new GameStateManager(this);
		
	}

	public void render() {
		
		Gdx.graphics.setTitle(TITLE + " -- FPS: " + Gdx.graphics.getFramesPerSecond());
		
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();
		MyInput.update();
		
	}
	
	public void dispose(){
		res.removeAll();
	}

	public void resize(int w, int h) {}

	public void resume() {}
	
	public void pause() {}

	
	public SpriteBatch getSpriteBatch(){
		return sb;
	}
	
	public BoundedCamera getCam(){
		return cam;
	}
	
	public OrthographicCamera getHudCam(){
		return hudCam;
	}
	
}
