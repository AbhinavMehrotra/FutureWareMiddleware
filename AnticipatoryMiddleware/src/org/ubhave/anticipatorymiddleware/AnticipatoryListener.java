package org.ubhave.anticipatorymiddleware;

import java.io.Serializable;
import java.util.Set;

import org.ubhave.anticipatorymiddleware.predictordata.PredictorData;

import android.content.Context;

public interface AnticipatoryListener extends Serializable{

	public void onNewEvent(Context app_context, int subscription_id, Set<PredictorData> data);
	
}
