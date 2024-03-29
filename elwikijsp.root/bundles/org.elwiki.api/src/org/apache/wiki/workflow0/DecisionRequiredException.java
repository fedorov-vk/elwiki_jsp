/* 
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.  
 */
package org.apache.wiki.workflow0;

import org.apache.wiki.api.exceptions.WikiException;

/**
 * Exception thrown when an activity -- that would otherwise complete silently --
 * cannot complete because a workflow {@link AbstractDecision} is required. The message
 * string should be a human-readable, internationalized String explaining why
 * the activity could not complete, or that the activity has been queued for 
 * review.
 *
 */
public class DecisionRequiredException extends WikiException
{

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     * @param message the message
     */
    public DecisionRequiredException( String message )
    {
        super( message );
    }
}
