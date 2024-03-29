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
package org.apache.wiki.api.ui;

import org.apache.wiki.api.core.Command;

/**
 * Placeholder class for all Commands.
 */
public interface AllCommands {

    /**
     * Returns a defensively-created array of all static Commands.
     *
     * @return the array of commands
     */
    static Command[] get() {
        return new Command[] {
            GroupCommand.DELETE_GROUP,
            GroupCommand.EDIT_GROUP,
            GroupCommand.VIEW_GROUP,
            PageCommand.COMMENT,
            PageCommand.CONFLICT,
            PageCommand.CREATE,
            PageCommand.DELETE,
            PageCommand.DIFF,
            PageCommand.EDIT,
            PageCommand.PAGE_ACL,
            PageCommand.EDIT_ACL,
            PageCommand.INFO,
            PageCommand.NONE,
            PageCommand.OTHER,
            PageCommand.PREVIEW,
            PageCommand.RENAME,
            PageCommand.RSS,
            PageCommand.VIEW,
            PageCommand.DELETE_ATTACHMENT,
            PageCommand.INFO_ATTACHMENT,
            //
            PageCommand.UPLOAD,
            PageCommand.ATTACH,
            PageCommand.ATTACHMENT_DOPOST,
            //
            //:FVK: PageCommand.VIEWID,
            RedirectCommand.REDIRECT,
            WikiCommand.CREATE_GROUP,
            WikiCommand.ERROR,
            WikiCommand.FIND,
            WikiCommand.INSTALL,
            WikiCommand.LOGIN,
            WikiCommand.LOGOUT,
            WikiCommand.LOSTPASSWORD,
            WikiCommand.MESSAGE,
            WikiCommand.PREFS,
            WikiCommand.WORKFLOW,
            WikiCommand.SCOPE,
            WikiCommand.PERSIST_CONTENT,
            WikiCommand.CHANGE_HIERARCHY,
            WikiCommand.ADMIN,
            WikiCommand.SECURE,
            WikiCommand.IMPORTPAGES,
        };
    }

}
