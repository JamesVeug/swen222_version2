package rendering;
/* Code for COMP261 Assignment
 * Author: pondy
 */


/** An immutable 3D vector or position.
 *  Note that it is safe to make the fields public because they
 *  are final and cannot be modified
 */
public class Vector3D{
    public float x;
    public float y;
    public float z;
    public float mag;
    public Vector3D normal = null;

    /** Construct a new vector, with the specified x, y, z components
     *  computes and caches the magnitude. */
    public Vector3D(float x, float y, float z){
	this.x = x;
	this.y = y;
	this.z = z;
	this.mag = (float) Math.sqrt(x*x +y*y + z*z);
    }

    /** A private constructor, used only within this class */
    private Vector3D(float x, float y, float z, float mag){
	this.x = x;
	this.y = y;
	this.z = z;
	this.mag = mag;
    }

    public Vector3D(Vector3D template) {
		this.x = template.x;
		this.y = template.y;
		this.z = template.z;
		this.mag = template.mag;
	}

	/** Constructs and returns a unit vector in the same direction
     *  as this vector.  */

    public Vector3D unitVector(){
	if(mag<=0.0)
	    return new Vector3D(1.0f, 0.0f, 0.0f, 1.0f);
	else
	    return new Vector3D(x/mag, y/mag, z/mag, 1.0f);
    }

    /** Returns the new vector that is this vector minus the other vector.  */
    public Vector3D minus(Vector3D other){
	return new Vector3D(x-other.x, y-other.y,  z-other.z);
    }

    /** Returns the new vector that is this vector plus the other vector.  */
    public Vector3D plus(Vector3D other){
	return new Vector3D(x+other.x, y+other.y,  z+other.z);
    }

    /** Returns the float that is the dot product of this vector and the other vector.  */
    public float dotProduct(Vector3D other){
	return x*other.x + y*other.y + z*other.z;
    }

    /** Returns the vector that is the cross product of this vector and the other vector.
     *  Note that the resulting vector is perpendicular to both this and the other vector.*/
    public Vector3D crossProduct(Vector3D other){
	float x = this.y * other.z - this.z * other.y;
	float y =this.z * other.x - this.x * other.z;
	float z = this.x * other.y - this.y * other.x;
	return new Vector3D( x, y, z);
    }

    /** Returns the cosine of the angle between this vector and the other vector. */
    public float cosTheta(Vector3D other){
	return (x*other.x + y*other.y + z*other.z)/mag/other.mag;
    }

    public String toString(){
        StringBuilder ans = new StringBuilder("Vect:");
	ans.append('(').append(x).append(',').append(y).append(',').append(z).append(')');
        return ans.toString();
    }

    public Vector3D leastX(Vector3D other){ return this.x <= other.x ? this : other; }
    public Vector3D leastY(Vector3D other){ return this.y <= other.y ? this : other; }
    public Vector3D leastZ(Vector3D other){ return this.z <= other.z ? this : other; }
    public Vector3D leastMag(Vector3D other){ return this.mag <= other.mag ? this : other; }

    public Vector3D mostX(Vector3D other){ return this.x > other.x ? this : other; }
    public Vector3D mostY(Vector3D other){ return this.y > other.y ? this : other; }
    public Vector3D mostZ(Vector3D other){ return this.z > other.z ? this : other; }
    public Vector3D mostMag(Vector3D other){ return this.mag > other.mag ? this : other; }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(mag);
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Vector3D))
			return false;
		Vector3D other = (Vector3D) obj;
		if (Float.floatToIntBits(mag) != Float.floatToIntBits(other.mag))
			return false;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}
}
