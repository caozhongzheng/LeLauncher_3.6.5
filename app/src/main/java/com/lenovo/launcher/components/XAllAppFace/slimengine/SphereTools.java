package com.lenovo.launcher.components.XAllAppFace.slimengine;

import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * 
 * 球形布局动画运算器
 * 
 * @author zhaoxy
 * 
 */
public class SphereTools {

	/** The value PI as a float. (180 degrees) */
	public static final float PI = (float) Math.PI;
	/** A value to multiply a degree value by, to convert it to radians. */
	public static final float DEG_TO_RAD = PI / 180.0f;
	/** A value to multiply a radian value by, to convert it to degrees. */
	public static final float RAD_TO_DEG = 180.0f / PI;
	private float radius = 500f;
	private float center_x = 250f;
	private float center_y = 250f;
	private float center_z = 260f;
	/**
	 * 经线
	 */
	private int lon_count = 8;
	/**
	 * 纬线
	 */
	private int lat_count = 4;

	// Temp
	private float ANGLE_LAT_PADDING = 22.5f;
	private float ANGLE_LON_PADDING = 22.5f;
	/**
	 * 两根经线间的夹角
	 */
	float angle_pre_lon = 360.0f / lon_count;
	/**
	 * 两根纬线间的夹角
	 */
	float angle_pre_lat = (180 - ANGLE_LAT_PADDING * 2) / (lat_count - 1);

	float angle_offset_h = 0;
	float angle_offset_v = 0;

	float target_width = 0;
	float target_height = 0;
	float angle_half_of_target_width = 0;
	float angle_half_of_target_height = 0;
	float pers = 0;

	/**
	 * 此模型和视件体为减少计算量，透视的投影面选为屏幕，视点选为屏幕外z轴上坐标为(0, 0, cameraZ).
	 * 
	 * @param x
	 *            球外接矩形左上角顶点x
	 * @param y
	 *            球外接矩形左上角顶点y
	 * @param radius
	 *            球半径
	 * @param sphereCenterZ
	 *            球心距离屏幕的深度，正数
	 * @param cameraZ
	 *            视点的z值，视点在屏幕外，正数
	 * @param lon_count
	 *            球面经线数量
	 * @param lat_count
	 *            球面纬线数量
	 * @param angleOfLatPadding
	 *            纬线上下padding角度
	 */
	public SphereTools(float x, float y, float radius, float sphereCenterZ,
			float cameraZ, int lon_count, int lat_count, float angleOfLatPadding) {
		this.radius = radius;
		this.center_x = x + radius;
		this.center_y = y + radius;
		this.center_z = sphereCenterZ;
		this.lon_count = lon_count;
		this.lat_count = lat_count;
		this.angle_pre_lon = (float)(360f / lon_count);
		this.ANGLE_LON_PADDING = angle_pre_lon / 2;
		this.ANGLE_LAT_PADDING = angleOfLatPadding;
		this.angle_pre_lat = (180 - ANGLE_LAT_PADDING * 2) / (lat_count - 1);
		this.pers = cameraZ;
	}

	public void setOffset(float horizontal, float vertical) {
		angle_offset_h = horizontal;
		angle_offset_v = vertical;
	}

	/**
	 * @param lon 经度，列
	 * @param lat 纬度，行
	 * @return
	 */
	public Vector3D getPoint(int lon, int lat) {
		float angle_to_z = (ANGLE_LON_PADDING + lon * angle_pre_lon - 90f + angle_offset_h)
				* DEG_TO_RAD;
		float angle_to_y = (ANGLE_LAT_PADDING + lat * angle_pre_lat)
				* DEG_TO_RAD;
		float x = (float) (radius * Math.sin(angle_to_y) * Math.sin(angle_to_z));
		float y = (float) (radius * Math.cos(angle_to_y));
		float z = (float) (radius * Math.sin(angle_to_y) * Math.cos(angle_to_z));
		return new Vector3D(x, y, z).rotateX(angle_offset_v * DEG_TO_RAD)
				.translate(new Vector3D(center_x, center_y, center_z));
	}

	public void setTargetDimension(float width, float height) {
		this.target_width = width;
		this.target_height = height;
		this.angle_half_of_target_width = width / radius / 2;
		this.angle_half_of_target_height = height / radius / 2;
	}

	/**
	 * @param lon 经度，列
	 * @param lat 纬度，行
	 * @return 指定矩形在球坐标系中的位置，返回其四个顶点的迪卡尔坐标
	 */
	public float[] getRect(int lon, int lat) {
		float[] result = new float[9];
		float temp_radius = radius;
		// float temp_radius = (float) Math.sqrt(radius * radius +
		// (target_width * target_width + target_height * target_height) /
		// 4);
		float temp_x = 0;
		float temp_y = 0;
		float temp_z = 0;
		float temp_zz = 0;
		float temp_angle_to_z = 0;
		float temp_angle_to_y = 0;
		float co = (float) Math.cos(angle_offset_v * DEG_TO_RAD);
		float si = (float) Math.sin(angle_offset_v * DEG_TO_RAD);
		float angle_to_z = (ANGLE_LON_PADDING + lon * angle_pre_lon - 90f + angle_offset_h)
				* DEG_TO_RAD;
		float angle_to_y = (ANGLE_LAT_PADDING + lat * angle_pre_lat)
				* DEG_TO_RAD;
		float z = (float) (radius * Math.sin(angle_to_y) * Math.cos(angle_to_z));
		// left top
		temp_angle_to_y = angle_to_y - angle_half_of_target_height;
		temp_angle_to_z = angle_to_z - angle_half_of_target_width;
		temp_x = (float) (temp_radius * Math.sin(temp_angle_to_y) * Math
				.sin(temp_angle_to_z));
		temp_y = (float) (temp_radius * Math.cos(temp_angle_to_y));
		temp_z = (float) (temp_radius * Math.sin(temp_angle_to_y) * Math
				.cos(temp_angle_to_z));
		temp_zz = (float) (co * temp_z - si * temp_y);
		temp_y = (float) (si * temp_z + co * temp_y);
		temp_x = temp_x / (1 - temp_zz / pers);
		temp_y = temp_y / (1 - temp_zz / pers);
		result[0] = temp_x + center_x;
		result[1] = center_y - temp_y;
		// right top
		temp_angle_to_z = angle_to_z + angle_half_of_target_width;
		temp_x = (float) (temp_radius * Math.sin(temp_angle_to_y) * Math
				.sin(temp_angle_to_z));
		temp_y = (float) (temp_radius * Math.cos(temp_angle_to_y));
		temp_z = (float) (temp_radius * Math.sin(temp_angle_to_y) * Math
				.cos(temp_angle_to_z));
		temp_zz = (float) (co * temp_z - si * temp_y);
		temp_y = (float) (si * temp_z + co * temp_y);
		temp_x = temp_x / (1 - temp_zz / pers);
		temp_y = temp_y / (1 - temp_zz / pers);
		result[2] = temp_x + center_x;
		result[3] = center_y - temp_y;
		// right bottom
		temp_angle_to_y = angle_to_y + angle_half_of_target_height;
		temp_x = (float) (temp_radius * Math.sin(temp_angle_to_y) * Math
				.sin(temp_angle_to_z));
		temp_y = (float) (temp_radius * Math.cos(temp_angle_to_y));
		temp_z = (float) (temp_radius * Math.sin(temp_angle_to_y) * Math
				.cos(temp_angle_to_z));
		temp_zz = (float) (co * temp_z - si * temp_y);
		temp_y = (float) (si * temp_z + co * temp_y);
		temp_x = temp_x / (1 - temp_zz / pers);
		temp_y = temp_y / (1 - temp_zz / pers);
		result[4] = temp_x + center_x;
		result[5] = center_y - temp_y;
		// left bottom
		temp_angle_to_z = angle_to_z - angle_half_of_target_width;
		temp_x = (float) (temp_radius * Math.sin(temp_angle_to_y) * Math
				.sin(temp_angle_to_z));
		temp_y = (float) (temp_radius * Math.cos(temp_angle_to_y));
		temp_z = (float) (temp_radius * Math.sin(temp_angle_to_y) * Math
				.cos(temp_angle_to_z));
		temp_zz = (float) (co * temp_z - si * temp_y);
		temp_y = (float) (si * temp_z + co * temp_y);
		temp_x = temp_x / (1 - temp_zz / pers);
		temp_y = temp_y / (1 - temp_zz / pers);
		result[6] = temp_x + center_x;
		result[7] = center_y - temp_y;
		result[8] = z;
		return result;
	}

    private Camera mCamera = new Camera();

    public Matrix getMatrix(int lon, int lat, Matrix result) {
        
        if (result == null) {
            result = new Matrix();
        }
        
        mCamera.save();
        float angle_to_z = ANGLE_LON_PADDING + lon * angle_pre_lon - 90f + angle_offset_h;
        float angle_to_y = ANGLE_LAT_PADDING + lat * angle_pre_lat - 90f;
        mCamera.translate(0.0f, 0.0f, 300);
        mCamera.rotateX(angle_offset_v);
        mCamera.rotateY(angle_to_z);
        mCamera.rotateX(angle_to_y);
        mCamera.translate(0.0f, 0.0f, -300);
        mCamera.getMatrix(result);
        result.preTranslate(-(center_x), -center_y);
        result.postTranslate(center_x, center_y);
        mCamera.restore();
        return result;
    }

	public class Vector3D {
		public float x;
		public float y;
		public float z;

		public Vector3D() {
			x = 0;
			y = 0;
			z = 0;
		}

		public Vector3D(float x, float y) {
			this(x, y, 0);
		}

		public Vector3D(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public String toString() {
			return "x = " + x + " y = " + y + " z = " + z;
		}

		public final Vector3D rotateX(float theta) {
			float co = (float) Math.cos(theta);
			float si = (float) Math.sin(theta);
			float zz = co * z - si * y;
			y = si * z + co * y;
			z = zz;
			return this;
		}

		public final Vector3D translate(Vector3D directionVector) {
			x = x + directionVector.x;
			y = y + directionVector.y;
			z = z + directionVector.z;
			return this;
		}
	}

}
