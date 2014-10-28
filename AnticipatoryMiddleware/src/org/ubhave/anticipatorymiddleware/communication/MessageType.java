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
package org.ubhave.anticipatorymiddleware.communication;

public class MessageType {

	public static final String REGISTRATION = "REGISTRATION";

	public static final String UPDATED_PRIVACY_POLICY = "UPDATED_PRIVACY_POLICY";

	public static final String REMOTE_PREDICTION_REQUEST = "REMOTE_PREDICTION_REQUEST";

	public static final String REMOTE_PREDICTION_RESPONSE = "REMOTE_PREDICTION_RESPONSE";

	public static final String UPDATED_PREDICTION_MODEL = "UPDATED_PREDICTION_MODEL";

	public static final String GROUP_PREDICTION_REQUEST = "GROUP_PREDICTION_REQUEST";

	public static final String GROUP_PREDICTION_RESPONSE = "GROUP_PREDICTION_RESPONSE";

	public static final String USER_CONTEXT = "USER_CONTEXT";

	public static final String Context_Life_Cycle_Period = "Context_Life_Cycle_Period";

	public static final String Context_SAMPLING_RATE = "Context_SAMPLING_RATE";

}
