/*******************************************************************************
 *
 * FutureWare Middleware
 *
 * Copyright (c) ${2014}, University of Birmingham
 * Abhinav Mehrotra, a.mehrotra@cs.bham.ac.uk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Birmingham 
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE ABOVE COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *******************************************************************************/
package org.ubhave.anticipatorymiddleware.subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ubhave.anticipatorymiddleware.AMException;

import android.util.Log;
import android.util.SparseArray;

public class SubscriptionList implements Serializable
{

	private static final long serialVersionUID = -2498790423997923110L;

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
	
	public SubscriptionList createSubscriptionList(List<Subscription> subscriptions){
		SubscriptionList s_list = new SubscriptionList();
		for(Subscription s: subscriptions){
			s_list.subscriptionMap.append(s.getId(), s);
		}
		return s_list;
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
