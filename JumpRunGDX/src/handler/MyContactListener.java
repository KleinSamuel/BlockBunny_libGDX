package handler;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener{

	private int numFootContacts;
	private Array<Body> bodiesToRemove;
	private boolean playerDead;
	
	public MyContactListener() {
		super();
		bodiesToRemove = new Array<Body>();
	}
	
	/**
	 * gets called when two fixtures start to collide
	 */
	@Override
	public void beginContact(Contact c) {
		
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();
		
		if(fa == null || fb == null) return;
		
		if(fa.getUserData() != null && fa.getUserData().equals("foot") || fb.getUserData() != null && fb.getUserData().equals("foot")){
			numFootContacts++;
		}
		
		if(fa.getUserData() != null && fa.getUserData().equals("crystal")){
			bodiesToRemove.add(fa.getBody());
		}
		if(fb.getUserData() != null && fb.getUserData().equals("crystal")){
			bodiesToRemove.add(fb.getBody());
		}
		
		if(fa.getUserData() != null && fa.getUserData().equals("spike")){
			playerDead = true;
		}
		if(fb.getUserData() != null && fb.getUserData().equals("spike")){
			playerDead = true;
		}
	}

	/**
	 * gets called when two fixtures are no longer colliding
	 */
	@Override
	public void endContact(Contact c) {
		
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();
		
		if(fa == null || fb == null) return;
		
		if(fa.getUserData() != null && fa.getUserData().equals("foot") || fb.getUserData() != null && fb.getUserData().equals("foot")){
			numFootContacts--;
		}
	}
	
	public boolean isPlayerOnGround(){
		return numFootContacts > 0;
	}
	
	public Array<Body> getBodiesToRemove(){
		return this.bodiesToRemove;
	}
	
	public boolean isPlayerDead(){
		return this.playerDead;
	}

	/**
	 * collision detection
	 * PRE SOLVE
	 * collision handling
	 */
	@Override
	public void preSolve(Contact c, Manifold m) {}
	
	/**
	 * collision detection
	 * collision handling
	 * POST SOLVE
	 */
	@Override
	public void postSolve(Contact c, ContactImpulse cImpulse) {}

}
