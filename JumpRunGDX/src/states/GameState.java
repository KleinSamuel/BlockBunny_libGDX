package states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import handler.BoundedCamera;
import handler.GameStateManager;
import main.Game;

public abstract class GameState {

	protected GameStateManager gsm;
	protected Game game;
	
	protected SpriteBatch sb;
	protected BoundedCamera cam;
	protected OrthographicCamera hudCam;
	
	protected GameState(GameStateManager gsm){
		this.gsm = gsm;
		game = gsm.game();
		sb = this.game.getSpriteBatch();
		cam = game.getCam();
		hudCam = game.getHudCam();
	}
	
	public abstract void handleInput();
	public abstract void update(float dt);
	public abstract void render();
	public abstract void dispose();
}
