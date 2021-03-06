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
package org.apache.wiki.ui.admin0;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.ui.GenericHTTPHandler;


/**
 *  Describes an administrative bean.
 *  
 *  @since  2.5.52
 */
public interface AdminBean extends GenericHTTPHandler {
	
    int UNKNOWN = 0;
    int CORE    = 1;
    int EDITOR  = 2;
    
    void initialize( Engine engine );
    
    /**
     *  Return a human-readable title for this AdminBean.
     *  
     *  @return the bean's title
     */
    String getTitle();
    
    /**
     *  Returns a type (UNKNOWN, EDITOR, etc).
     *  
     *  @return the bean's type
     */
    int getType();
}
