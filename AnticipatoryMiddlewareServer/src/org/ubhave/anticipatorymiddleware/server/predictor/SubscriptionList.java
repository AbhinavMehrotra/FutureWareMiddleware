package org.ubhave.anticipatorymiddleware.server.predictor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ubhave.anticipatorymiddleware.AMException;

import android.util.Log;
import android.util.SparseArray;

public class SubscriptionList
{
	private static String TAG = "SubscriptionList";

	private final SparseArray<Subscription> subscriptionMap;
	
	private final Random keyGenerator;

	public SubscriptionList()
	{
		subscriptionMap = new SparseArray<Subscription>();
		keyGenerator = new Random();
	}

	public synchronized int registerSubscription(Subscription s) throws AMException
	{
		if (subscriptionMap.indexOfValue(s)>=0) // subscription already exists
		{
			Log.d(TAG, "Inside registerSubscription() subscription already exists");
			for (int i = 0; i < subscriptionMap.size(); i++)
			{
				int subscriptionId = subscriptionMap.keyAt(i);
				Subscription subscription = subscriptionMap.get(subscriptionId);
				if (subscription.equals(s))
				{
					return subscriptionId;
				}
			}
			throw new AMException(AMException.INVALID_STATE, "Registered Subscription not found.");
		}
		else
		{
			int subscriptionId = randomKey();
			s.setId(subscriptionId);
			subscriptionMap.append(subscriptionId, s);
			Log.d(TAG, "Inside registerSubscription() new subscription created, id:"+subscriptionId);
			return subscriptionId;
		}
	}

	public synchronized Subscription removeSubscription(int subscriptionId)
	{
		Subscription s = subscriptionMap.get(subscriptionId);
		if (s == null)
		{
			Log.d(TAG, "removeSubscription() invalid subscription id: " + subscriptionId);
			return null;
		}
		else
		{
			Log.d(TAG, "removeSubscription() deleting subscription created");
			subscriptionMap.delete(subscriptionId);
		}
		return s;
	}

	public synchronized List<Subscription> getAllSubscriptions()
	{
		ArrayList<Subscription> list = new ArrayList<Subscription>();
		Log.d("LOG", "List size is: " + subscriptionMap.size());

		for (int i = 0; i < subscriptionMap.size(); i++)
		{
			Subscription sub = subscriptionMap.valueAt(i);
			if (sub != null)
			{
				list.add(sub);
			}
		}
		return list;
	}

	public synchronized Subscription getSubscription(int subscriptionId)
	{
		return subscriptionMap.get(subscriptionId);
	}

	private int randomKey() throws AMException
	{
		int subscriptionId = keyGenerator.nextInt();
		int loopCount = 0;
		while (subscriptionMap.get(subscriptionId) != null)
		{
			if (loopCount > 1000)
				throw new AMException(AMException.INVALID_STATE, "Listener map >1000 key conflicts.");
			subscriptionId = keyGenerator.nextInt();
			loopCount++;
		}
		return subscriptionId;
	}

	
}
