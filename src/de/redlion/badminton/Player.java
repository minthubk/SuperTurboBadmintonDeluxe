package de.redlion.badminton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Player {

	public enum STATE {
		IDLE, UP, DOWN, LEFT, RIGHT, DOWNLEFT, UPLEFT, DOWNRIGHT, UPRIGHT, AIMING;
	}
	
	public enum AIMING {
		IDLE, UP, DOWN, LEFT, RIGHT, DOWNLEFT, UPLEFT, DOWNRIGHT, UPRIGHT;
	}

	public Vector3 direction = new Vector3(0, 0, -1);
	public Vector3 position = new Vector3(-2, 7, -0.5f);
	public Vector3 velocity = new Vector3(0,0,0); //for jumps
	public STATE state = STATE.IDLE;
	public AIMING aiming = AIMING.IDLE;
	public boolean jump = false;
	public float aimTime = 1;
	public float momentum = 63.3f;

	public Player() {
	}

	public void update() {
		if (state == STATE.LEFT) {
			position.x = position.x - Gdx.graphics.getDeltaTime() * 4;
			direction = new Vector3(-1, 0, 0);
		}
		if (state == STATE.RIGHT) {
			position.x = position.x + Gdx.graphics.getDeltaTime() * 4;
			direction = new Vector3(1, 0, 0);
		}
		if (state == STATE.UP) {
			position.y = position.y - Gdx.graphics.getDeltaTime() * 4;
			direction = new Vector3(0, -1, 0);
		}
		if (state == STATE.DOWN) {
			position.y = position.y + Gdx.graphics.getDeltaTime() * 4;
			direction = new Vector3(0, 1, 0);
		}
		if (state == STATE.DOWNLEFT) {
			position.x = position.x - Gdx.graphics.getDeltaTime() * 3.5f;
			position.y = position.y + Gdx.graphics.getDeltaTime() * 3.5f;
			direction = new Vector3(-1, 1, 0);
		}
		if (state == STATE.UPLEFT) {
			position.x = position.x - Gdx.graphics.getDeltaTime() * 3.5f;
			position.y = position.y - Gdx.graphics.getDeltaTime() * 3.5f;
			direction = new Vector3(-1, -1, 0);
		}
		if (state == STATE.DOWNRIGHT) {
			position.x = position.x + Gdx.graphics.getDeltaTime() * 3.5f;
			position.y = position.y + Gdx.graphics.getDeltaTime() * 3.5f;
			direction = new Vector3(1, 1, 0);
		}
		if (state == STATE.UPRIGHT) {
			position.x = position.x + Gdx.graphics.getDeltaTime() * 3.5f;
			position.y = position.y - Gdx.graphics.getDeltaTime() * 3.5f;
			direction = new Vector3(1, -1, 0);
		}
		if (state == STATE.IDLE) {
			aimTime=1;
			if(Math.abs(direction.x) < 0.001f && Math.abs(direction.y) < 0.001f)
				direction = new Vector3(0, 0, 0);
		}
		if(state == STATE.AIMING) {
			aimTime += Gdx.graphics.getDeltaTime() / 5;
			
			if (aiming == AIMING.LEFT) {
				position.x = position.x - Gdx.graphics.getDeltaTime() * 0.5f;
			}
			if (aiming == AIMING.RIGHT) {
				position.x = position.x + Gdx.graphics.getDeltaTime() * 0.5f;
			}
			if (aiming == AIMING.UP) {
				position.y = position.y - Gdx.graphics.getDeltaTime() * 0.5f;
			}
			if (aiming == AIMING.DOWN) {
				position.y = position.y + Gdx.graphics.getDeltaTime() * 0.5f;
			}
			if (aiming == AIMING.DOWNLEFT) {
				position.x = position.x - Gdx.graphics.getDeltaTime() * 0.4375f;
				position.y = position.y + Gdx.graphics.getDeltaTime() * 0.4375f;
			}
			if (aiming == AIMING.UPLEFT) {
				position.x = position.x - Gdx.graphics.getDeltaTime() * 0.4375f;
				position.y = position.y - Gdx.graphics.getDeltaTime() * 0.4375f;
			}
			if (aiming == AIMING.DOWNRIGHT) {
				position.x = position.x + Gdx.graphics.getDeltaTime() * 0.4375f;
				position.y = position.y + Gdx.graphics.getDeltaTime() * 0.4375f;
			}
			if (aiming == AIMING.UPRIGHT) {
				position.x = position.x + Gdx.graphics.getDeltaTime() * 0.4375f;
				position.y = position.y - Gdx.graphics.getDeltaTime() * 0.4375f;
			}
			
		}
		
		direction.mul( (float) Math.pow(0.97f, Gdx.graphics.getDeltaTime() * momentum));
		position.add(direction.cpy().mul(Gdx.graphics.getDeltaTime()));
		
		
		if(jump){
			velocity.mul( (float) Math.pow(0.97f, Gdx.graphics.getDeltaTime() * 35.f));
			position.add(velocity.x * Gdx.graphics.getDeltaTime(), velocity.y * Gdx.graphics.getDeltaTime(), velocity.z * Gdx.graphics.getDeltaTime());
			if(position.z <= -0.5f) {
				position.z = position.z + (Gdx.graphics.getDeltaTime() * 3.3f);
			}
			else if(position.z > -0.5f) {
				jump = false;
			}
			
		}

	}
	
	public void jump() {
		jump=true;
		velocity = direction;
		velocity.x *= 6.0f/5;
		velocity.y *= 6.0f/2;
		velocity.z = -6.0f;
	}
	
	public String toString() {
		return "Player State: " + state + " Aiming: " + aiming + " Position: " + position + " Direction: " + direction;
	}

}
