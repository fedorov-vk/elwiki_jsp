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

package org.apache.wiki.api.event;

import  java.util.EventListener;

import org.apache.wiki.api.event.WikiEvent;


/**
  * Defines an interface for an object that listens for WikiEvents.
  *
  * @since   2.3.92
  */
public interface WikiEventListener extends EventListener {

    /**
     * Fired when a WikiEvent is triggered by an event source.
     *
     * @param event a WikiEvent object
     */
    void actionPerformed( WikiEvent event );

}
