package handler;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

public class MyInputProcessor extends InputAdapter{

	public boolean mouseMoved(int x, int y){
		MyInput.x = x;
		MyInput.y = y;
		return true;
	}
	
	public boolean touchDragged(int x, int y, int pointer){
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		return true;
	}
	
	public boolean touchDown(int x, int y, int pointer, int button){
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		return true;
	}
	
	public boolean touchUp(int x, int y, int pointer, int button){
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = false;
		return true;
	}
	
	public boolean keyDown(int keycode) {
		if(keycode == Keys.W){
			MyInput.setkey(MyInput.BUTTON1, true);
		}
		if(keycode == Keys.S){
			MyInput.setkey(MyInput.BUTTON2, true);
		}
		return true;
	}
	
	public boolean keyUp(int keycode) {
		if(keycode == Keys.W){
			MyInput.setkey(MyInput.BUTTON1, false);
		}
		if(keycode == Keys.S){
			MyInput.setkey(MyInput.BUTTON2, false);
		}
		return true;
	}
	
}
