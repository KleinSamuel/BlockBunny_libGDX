package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

import main.Game;

public class Crystal extends B2DSprite{

	public Crystal(Body body) {
		super(body);
		
		Texture tex = Game.res.getTexture("crystal");
		TextureRegion[] sprites = TextureRegion.split(tex,16, 16)[0];
		
		setAnimation(sprites, 1 / 12f);
		
		width = sprites[0].getRegionWidth();
		height = sprites[0].getRegionHeight();
	}

	
}
