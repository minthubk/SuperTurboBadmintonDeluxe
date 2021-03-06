package de.redlion.badminton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import de.redlion.badminton.controls.ControlMappings;
import de.redlion.badminton.controls.PlayerOneControlMappings;

public class Player {

	public enum STATE {
		IDLE, UP, DOWN, LEFT, RIGHT, DOWNLEFT, UPLEFT, DOWNRIGHT, UPRIGHT, AIMING;
	}
	
	public enum AIMING {
		IDLE, UP, DOWN, LEFT, RIGHT, DOWNLEFT, UPLEFT, DOWNRIGHT, UPRIGHT;
	}
	
	public enum SIDE {
		TOP, BOTTOM;
	}
	
	public ControlMappings input;

	final static float SPEED = 8;
	final static float MOMENTUM = 0.85f;
	
	public SIDE side = SIDE.BOTTOM;
	
	public Vector3 direction = new Vector3(0, 0, 0);
	public Vector3 lastDirection = new Vector3(0, 0, 0);
	public Vector3 position = new Vector3(-2, 0, 7);
	public Vector3 velocity = new Vector3(0, 0, 0);
	public STATE state = STATE.IDLE;
	public AIMING aiming = AIMING.IDLE;
	public boolean service = false;
	
	//charge time
	public float aimTime = 1;
	public float diagonalTime = 0; //currently only used for sliding diagonally  when player didn't release both keys at the same time
	public float moveTime = 0.0f;
	public float keyframeAnimTime = 0;
	
	private BoundingBox borders;

	public Player(SIDE side, boolean service) {		
		this.side = side;
		this.service = service;
		if(this.service) {
			state = STATE.AIMING;
		}
		if(side == SIDE.TOP) {
			position = new Vector3(0, 0, -3);
		}
		input = new PlayerOneControlMappings();
	}

	public void update() {
		keyframeAnimTime += Gdx.graphics.getDeltaTime() * 1f;
		
		borders = GameSession.getInstance().borders;
		
		if(state != STATE.AIMING) {
			
			if (state == STATE.LEFT) {
				position.x = position.x - Gdx.graphics.getDeltaTime() * SPEED + velocity.x;
				position.z += velocity.z;
				if(diagonalTime > 1.0f && (direction.idt(new Vector3(-1,1,0)) || direction.idt(new Vector3(-1,-1,0)))) {
					direction = new Vector3(-1, 0, 0);
					diagonalTime = -1;
				}
				else if (!direction.idt(new Vector3(-1,1,0)) && !direction.idt(new Vector3(-1,-1,0)))
					direction = new Vector3(-1, 0, 0);
			}
			if (state == STATE.RIGHT) {
				position.x = position.x + Gdx.graphics.getDeltaTime() * SPEED + velocity.x;
				position.z += velocity.z;
				if(diagonalTime > 1.0f && (direction.idt(new Vector3(1,1,0)) || direction.idt(new Vector3(1,-1,0)))) {
					direction = new Vector3(1, 0, 0);
					diagonalTime = -1;
				}
				else if(!direction.idt(new Vector3(1,1,0)) && !direction.idt(new Vector3(1,-1,0)))
					direction = new Vector3(1, 0, 0);
			}
			if (state == STATE.UP) {
				position.x += velocity.x;
				position.z = position.z - Gdx.graphics.getDeltaTime() * SPEED * 2 + velocity.z;
				if(diagonalTime > 1.0f && (direction.idt(new Vector3(-1,-1,0)) || direction.idt(new Vector3(1,-1,0)))) {
					direction = new Vector3(0, 0, -1);
					diagonalTime = -1;
				}
				else if(!direction.idt(new Vector3(-1,-1,0)) && !direction.idt(new Vector3(1,-1,0))) {
					direction = new Vector3(0, 0, -1);
				}
			}
			if (state == STATE.DOWN) {
				position.x += velocity.x;
				position.z = position.z + Gdx.graphics.getDeltaTime() * SPEED * 2 + velocity.z;
				if(diagonalTime > 1.0f && (direction.idt(new Vector3(-1,1,0)) || direction.idt(new Vector3(1,1,0)))) {
					direction = new Vector3(0, 0, 1);
					diagonalTime = -1;
				}
				else if(!direction.idt(new Vector3(-1,1,0)) && !direction.idt(new Vector3(1,1,0)))
					direction = new Vector3(0, 0, 1);
			}
			if (state == STATE.DOWNLEFT) {
				position.x = position.x - Gdx.graphics.getDeltaTime() * SPEED + velocity.x;
				position.z = position.z + Gdx.graphics.getDeltaTime() * SPEED*2 + velocity.z;
				direction = new Vector3(-1, 0, 1);
				diagonalTime = 0;
			}
			if (state == STATE.UPLEFT) {
				position.x = position.x - Gdx.graphics.getDeltaTime() * SPEED + velocity.x;
				position.z = position.z - Gdx.graphics.getDeltaTime() * SPEED*2 + velocity.z;
				direction = new Vector3(-1, 0, -1);
				diagonalTime = 0;
			}
			if (state == STATE.DOWNRIGHT) {
				position.x = position.x + Gdx.graphics.getDeltaTime() * SPEED + velocity.x;
				position.z = position.z + Gdx.graphics.getDeltaTime() * SPEED*2 + velocity.z;
				direction = new Vector3(1, 0, 1);
				diagonalTime = 0;
			}
			if (state == STATE.UPRIGHT) {
				position.x = position.x + Gdx.graphics.getDeltaTime() * SPEED + velocity.x;
				position.z = position.z - Gdx.graphics.getDeltaTime() * SPEED*2 + velocity.z;
				direction = new Vector3(1, 0, -1);
				diagonalTime = 0;
			}
			if (state == STATE.IDLE) {
				aimTime=1;
				
				velocity.mul(MOMENTUM);
				position.x += velocity.x;
				position.z += velocity.z;
				
				if(diagonalTime > 0.0f)
					diagonalTime -= Gdx.graphics.getDeltaTime() / 2;
				else
					diagonalTime = 0;
				
				if(Math.abs(velocity.x) < 0.01f && Math.abs(velocity.z) < 0.01f) {
					direction = new Vector3(0, 0, 0);
					velocity = new Vector3(0, 0, 0);
					moveTime = 0.0f;
				}
			}
			else {
				moveTime += Gdx.graphics.getDeltaTime();
				velocity.add(direction.cpy().mul(Gdx.graphics.getDeltaTime()  * moveTime * 0.5f));
				if(direction.x == 0 && state != STATE.DOWNLEFT && state != STATE.DOWNRIGHT && state != STATE.UPRIGHT && state != STATE.UPLEFT)
					velocity.x *= 0.8f;
				if(direction.z == 0 && state != STATE.DOWNLEFT && state != STATE.DOWNRIGHT && state != STATE.UPRIGHT && state != STATE.UPLEFT)
					velocity.z *= 0.8f;
				if(Math.abs(velocity.x) > 0.05f)
					velocity.x = Math.signum(velocity.x) * 0.05f;
				if(Math.abs(velocity.y) > 0.1f)
					velocity.z = Math.signum(velocity.y) * 0.1f;
			}
			if(diagonalTime > 1.0f)
				diagonalTime = -1;
			if(moveTime > 0.5f)
				moveTime = 0.5f;
			
		}
		if(state == STATE.AIMING) {
			aimTime += Gdx.graphics.getDeltaTime() / 3;
			
			if(aiming != AIMING.IDLE)
				velocity.mul(0.8f);
			
			if(diagonalTime > 0.0f)
				diagonalTime -= Gdx.graphics.getDeltaTime();
			else
				diagonalTime = 0;
			
			if (aiming == AIMING.LEFT) {
				direction = new Vector3(-1, 0, 0);
				position.x = position.x - Gdx.graphics.getDeltaTime() * 0.5f + velocity.x;
				position.z += velocity.z;
			}
			if (aiming == AIMING.RIGHT) {
				direction = new Vector3(1, 0, 0);
				position.x = position.x + Gdx.graphics.getDeltaTime() * 0.5f + velocity.x;
				position.z += velocity.z;
			}
			if (aiming == AIMING.UP) {
				direction = new Vector3(0, -1, 0);
				position.z += velocity.x;
				position.z = position.z - Gdx.graphics.getDeltaTime() * 0.5f + velocity.z;
			}
			if (aiming == AIMING.DOWN) {
				direction = new Vector3(0, 1, 0);
				position.z += velocity.x;
				position.z = position.z + Gdx.graphics.getDeltaTime() * 0.5f + velocity.z;
			}
			if (aiming == AIMING.DOWNLEFT) {
				direction = new Vector3(-1, 1, 0);
				position.x = position.x - Gdx.graphics.getDeltaTime() * 0.4375f + velocity.x;
				position.z = position.z + Gdx.graphics.getDeltaTime() * 0.4375f + velocity.z;
			}
			if (aiming == AIMING.UPLEFT) {
				direction = new Vector3(-1, -1, 0);
				position.x = position.x - Gdx.graphics.getDeltaTime() * 0.4375f + velocity.x;
				position.z = position.z - Gdx.graphics.getDeltaTime() * 0.4375f + velocity.z;
			}
			if (aiming == AIMING.DOWNRIGHT) {
				direction = new Vector3(1, 1, 0);
				position.x = position.x + Gdx.graphics.getDeltaTime() * 0.4375f + velocity.x;
				position.z = position.z + Gdx.graphics.getDeltaTime() * 0.4375f + velocity.z;
			}
			if (aiming == AIMING.UPRIGHT) {
				direction = new Vector3(1, -1, 0);
				position.x = position.x + Gdx.graphics.getDeltaTime() * 0.4375f + velocity.x;
				position.z = position.z - Gdx.graphics.getDeltaTime() * 0.4375f + velocity.z;
			}
			
			if(aiming == AIMING.IDLE) {
				
				velocity.mul(MOMENTUM);
				position.x += velocity.x;
				position.z += velocity.z;
				
				if(Math.abs(velocity.x) < 0.01f && Math.abs(velocity.z) < 0.01f) {
					direction = new Vector3(0, 0, 0);
					velocity = new Vector3(0, 0, 0);
					moveTime = 0.0f;
				}
			}
			
			if(Math.abs(velocity.x) < 0.01f && Math.abs(velocity.z) < 0.01f) {
				direction = new Vector3(0, 0, 0);
				velocity = new Vector3(0, 0, 0);
				moveTime = 0.0f;
			}	
			
		}
		else if(state != STATE.IDLE && state != STATE.DOWNLEFT && state != STATE.UPRIGHT && state != STATE.UPLEFT && state != STATE.DOWNRIGHT) {
			if(diagonalTime != -1 )
				diagonalTime += Gdx.graphics.getDeltaTime() * 14;
		}
		
		//Out of bounds?
		if (side == SIDE.BOTTOM) {
			if (position.z < 1.5f) {
				position.z = 1.5f;
			}

			if (position.z > borders.max.z - 1.5f) {
				position.z = borders.max.z - 1.5f;
			}
		}
		if (side == SIDE.TOP) {
			if (position.z > -1.5f) {
				position.z = -1.5f;
			}

			if (position.z < borders.min.z + 1.5f) {
				position.z = borders.min.z + 1.5f;
			}
		}
		if (position.x > borders.max.x - 1.5f) {
			position.x = borders.max.x - 1.5f;
		}
		if (position.x < borders.min.x + 1.5f) {
			position.x = borders.min.x + 1.5f;
		}

	}
	
	public void switchState() {
		if(this.state == STATE.AIMING) {
			int tmp = this.aiming.ordinal();
			this.aimTime = 1;
			this.aiming = Player.AIMING.IDLE;
			this.state = Player.STATE.values()[tmp];
		} else {
			int tmp = this.state.ordinal();
			this.aimTime = 1;
			this.aiming = Player.AIMING.values()[tmp];
			this.state = Player.STATE.AIMING;
		}
		
	}
	
	public String toString() {
		return "Player " + " State: " + state + " Aiming: " + aiming + " Position: "
				+ (float) MathUtils.round(position.x * 10) / 10. + " " 
				+ (float) MathUtils.round(position.y * 10) / 10. + " "
				+ (float) MathUtils.round(position.z * 10) / 10. + " Direction: "
				+ (float) MathUtils.round(direction.x * 10) / 10. + " " 
				+ (float) MathUtils.round(direction.y * 10) / 10. + " "
				+ (float) MathUtils.round(direction.z * 10) / 10.;
	}

}
