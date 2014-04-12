package org.ubhave.anticipatorymiddleware.server.communication;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;
import org.ubhave.anticipatorymiddleware.server.AMException;
import org.ubhave.anticipatorymiddleware.server.AnticipatoryManager;
import org.ubhave.anticipatorymiddleware.server.database.MongoDBManager;
import org.ubhave.anticipatorymiddleware.server.datastack.GroupPredictionRequestStack;
import org.ubhave.anticipatorymiddleware.server.events.EventMnager;
import org.ubhave.anticipatorymiddleware.server.privacy.PrivacyManager;

/**
 * Message parser for message received from the clients via TCP socket
 */
public class MessageParser {

	/**
	 * Parses the message received via TCP socket and takes actions to handle it
	 * @param message
	 */
	protected static void run(String message){
		try {
			JSONObject obj= new JSONObject(message);
			String data_type= obj.getString("DATA_TYPE");
			//String user_id = obj.getString("USER_ID");
			int data_type_value = getDataTypeValue(data_type);
			System.out.println("data_type: "+data_type);
			System.out.println("data_type_value: "+data_type_value);
			switch (data_type_value) {
			case 1:
				//GROUP_PREDICTION_REQUEST
				PrivacyManager.onNewGroupPredictionRequest(obj, true);
				break;
			case 2:
				//GROUP_PREDICTION_RESPONSE
				//This will not be received instead this is to be transmitted to the user who made the request
				break;
			case 3:
				//REGISTRATION
				try {
					MongoDBManager mongo_db = AnticipatoryManager.getInstance().getMongoDBManager();
					mongo_db.registerUser(obj);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (AMException e) {
					e.printStackTrace();
				}
				GroupPredictionRequestStack.onNewUserOrPrivacyModification();
				break;
			case 4:
				//REMOTE_PREDICTION_REQUEST
				//This will not be received instead this is to be transmitted to the friends of the user
				break;
			case 5:
				//REMOTE_PREDICTION_RESPONSE
				new EventMnager().onNewRemotePredictionResponse(obj);
				break;
			case 6:
				//UPDATED_PREDICTION_MODEL
				try {
					MongoDBManager mongo_db = AnticipatoryManager.getInstance().getMongoDBManager();
					mongo_db.updateUserPredictionModel(obj);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (AMException e) {
					e.printStackTrace();
				}
				break;				

			case 7:
				//UPDATED_PRIVACY_POLICY
				try {
					MongoDBManager mongo_db = AnticipatoryManager.getInstance().getMongoDBManager();
					mongo_db.updateUserPrivacyPolicy(obj);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (AMException e) {
					e.printStackTrace();
				}
				GroupPredictionRequestStack.onNewUserOrPrivacyModification();
				break;
				
			case 8:
				//USER_CONTEXT
				try {
					MongoDBManager mongo_db = AnticipatoryManager.getInstance().getMongoDBManager();
					mongo_db.updateUserContext(obj);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (AMException e) {
					e.printStackTrace();
				}
				break;
				
			case 9:
				//Context_SAMPLING_RATE
				try {
					MongoDBManager mongo_db = AnticipatoryManager.getInstance().getMongoDBManager();
					mongo_db.updateContextSamplingRate(obj);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (AMException e) {
					e.printStackTrace();
				}
				break;
				
			case 10:
				//Context_Life_Cycle_Period
				try {
					MongoDBManager mongo_db = AnticipatoryManager.getInstance().getMongoDBManager();
					mongo_db.updateContextLifeCyclePeriod(obj);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (AMException e) {
					e.printStackTrace();
				}
				break;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static int getDataTypeValue(String name){
		if(name.equalsIgnoreCase(MessageType.GROUP_PREDICTION_REQUEST)) return 1;
		if(name.equalsIgnoreCase(MessageType.GROUP_PREDICTION_RESPONSE)) return 2;
		if(name.equalsIgnoreCase(MessageType.REGISTRATION)) return 3;
		if(name.equalsIgnoreCase(MessageType.REMOTE_PREDICTION_REQUEST)) return 4;
		if(name.equalsIgnoreCase(MessageType.REMOTE_PREDICTION_RESPONSE)) return 5;	
		if(name.equalsIgnoreCase(MessageType.UPDATED_PREDICTION_MODEL)) return 6;	
		if(name.equalsIgnoreCase(MessageType.UPDATED_PRIVACY_POLICY)) return 7;			
		if(name.equalsIgnoreCase(MessageType.USER_CONTEXT)) return 8;				
		if(name.equalsIgnoreCase(MessageType.Context_SAMPLING_RATE)) return 9;				
		if(name.equalsIgnoreCase(MessageType.Context_Life_Cycle_Period)) return 10;		
		return 0;
	}


}
