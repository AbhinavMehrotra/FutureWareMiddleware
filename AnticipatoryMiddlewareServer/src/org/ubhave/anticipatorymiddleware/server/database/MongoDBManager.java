package org.ubhave.anticipatorymiddleware.server.database;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.AMException;
import org.ubhave.anticipatorymiddleware.server.communication.JSONKeys;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBManager {

	private final DB db;

	public MongoDBManager(String mongo_db_ip, String mongo_db_name, String mongo_db_user_name, String mongo_db_password) throws AMException, UnknownHostException {
		MongoClient mongoClient = new MongoClient(mongo_db_ip);
		this.db = mongoClient.getDB(mongo_db_name);
		boolean authentication_result = db.authenticate(mongo_db_user_name, mongo_db_password.toCharArray());
		if(!authentication_result)
			throw new AMException(AMException.INVALID_PARAMETER, "MongoDB authentication failure.");
	}	


	public void registerUser(JSONObject obj) throws JSONException{
		String user_id = obj.getString(JSONKeys.USER_ID);
		int context_sampling_rate = obj.getInt(JSONKeys.CONTEXT_SAMPLING_RATE);
		int context_life_cycle_period = obj.getInt(JSONKeys.CONTEXT_LIFE_CYCLE_PERIOD);
		JSONObject privacy_policy = obj.getJSONObject(MongoDBUtils.Key_Type_User_Privacy_Policy);
		JSONObject osn_ids = obj.getJSONObject(MongoDBUtils.Key_Type_User_OSN_IDS);
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Registration);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old one
			coll.remove(cursor.curr());
		}
		doc.append(MongoDBUtils.Key_Type_User_Context_Sampling_Rate, context_sampling_rate);
		doc.append(MongoDBUtils.Key_Type_User_Context_Life_Cycle_Period, context_life_cycle_period);
		doc.append(MongoDBUtils.Key_Type_User_Privacy_Policy, privacy_policy).
		append(MongoDBUtils.Key_Type_User_OSN_IDS, osn_ids);
		coll.save(doc);
	}

	public void updateUserPrivacyPolicy(JSONObject obj) throws JSONException{
		String user_id = obj.getString(MongoDBUtils.Key_Type_User_Id);
		JSONObject privacy_policy = obj.getJSONObject(MongoDBUtils.Key_Type_User_Privacy_Policy);
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Registration);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old privacy data
			DBObject db_object = cursor.curr();
			db_object.removeField(MongoDBUtils.Key_Type_User_Privacy_Policy);
			db_object.put(MongoDBUtils.Key_Type_User_Privacy_Policy, privacy_policy);
			coll.save(db_object);
		}
		else{
			doc.append(MongoDBUtils.Key_Type_User_Privacy_Policy, privacy_policy);
			coll.save(doc);
		}
	}

	public int getUserContextSamplingRate(String user_id){
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Registration);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old privacy data
			DBObject db_object = cursor.curr();
			return (int) db_object.get(MongoDBUtils.Key_Type_User_Context_Sampling_Rate);
		}
		return 0;
	}

	public int getUserContextLifeCyclePeriod(String user_id){
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Registration);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old privacy data
			DBObject db_object = cursor.curr();
			return (int) db_object.get(MongoDBUtils.Key_Type_User_Context_Life_Cycle_Period);
		}
		return 0;
	}

	public JSONObject getUserPrivacyPolicy(String user_id){
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Registration);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old privacy data
			DBObject db_object = cursor.curr();
			return (JSONObject) db_object.get(MongoDBUtils.Key_Type_User_Privacy_Policy);
		}
		return null;
	}	

	public void updateUserPredictionModel(JSONObject obj) throws JSONException{
		String user_id = obj.getString(MongoDBUtils.Key_Type_User_Id);
		JSONObject prediction_data = obj.getJSONObject(MongoDBUtils.Key_Type_User_Prediction_Data);
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Prediction_Model);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old prediction data
			DBObject db_object = cursor.curr();
			db_object.removeField(MongoDBUtils.Key_Type_User_Prediction_Data);
			db_object.put(MongoDBUtils.Key_Type_User_Prediction_Data, prediction_data);
			coll.save(db_object);
		}
		else{
			doc.append(MongoDBUtils.Key_Type_User_Prediction_Data, prediction_data);
			coll.save(doc);
		}
	}	

	public JSONObject geteUserPredictionModel(String user_id){
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Prediction_Model);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old privacy data
			DBObject db_object = cursor.curr();
			return (JSONObject) db_object.get(MongoDBUtils.Key_Type_User_Prediction_Data);
		}
		return null;
	}

	public void updateUserContext(JSONObject obj) throws JSONException{
		String user_id = obj.getString(MongoDBUtils.Key_Type_User_Id);
		int sensor_type = obj.getInt(JSONKeys.SENSOR_TYPE);
		String context = obj.getString(JSONKeys.USER_CONTEXT);
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Context);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old prediction data
			DBObject db_object = cursor.curr();
			db_object.removeField(MongoDBUtils.Key_Type_Sensor_Type(sensor_type));
			db_object.put(MongoDBUtils.Key_Type_Sensor_Type(sensor_type), context);
			coll.save(db_object);
		}
		else{
			doc.append(MongoDBUtils.Key_Type_Sensor_Type(sensor_type), context);
			coll.save(doc);
		}
	}	

	public String geteUserContext(String user_id, int sensor_type){
		DBCollection coll = db.getCollection(MongoDBUtils.Collection_Type_User_Prediction_Model);
		BasicDBObject doc=new BasicDBObject();
		doc.append(MongoDBUtils.Key_Type_User_Id, user_id);
		DBCursor cursor = coll.find(doc);
		if(cursor.count()>0){
			//if user registration is already present then remove the old privacy data
			DBObject db_object = cursor.curr();
			return (String) db_object.get(MongoDBUtils.Key_Type_Sensor_Type(sensor_type));
		}
		return null;
	}
	
}
