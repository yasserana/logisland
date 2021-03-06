/**
 * Copyright (C) 2016 Hurence (support@hurence.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hurence.logisland.connect.opc;


public interface OpcRecordFields {

    /**
     * The update period in milliseconds.
     */
    String SAMPLING_RATE = "sampling_rate_millis";
    /**
     * The timestamp when the OPC server acquired data.
     */
    String SOURCE_TIMESTAMP = "tag_source_timestamp";
    /**
     * The timestamp when the value has been created.
     */
    String SAMPLED_TIMESTAMP = "tag_sampled_timestamp";

    /**
     * The internal tag id (depends to the implementation).
     */
    String TAG_ID = "tag_id";
    /**
     * The quality of the measurement (in case server caching is used).
     * The value is managed by the OPC server.
     */
    String QUALITY = "quality";
    /**
     * The record value. Can be missing in case an error occurred.
     */
    String VALUE = "record_value";
    /**
     * The OPC server error code in case the tag reading is in error.
     */
    String ERROR_CODE = "error_code";
    /**
     * The error reason (as string description)
     */
    String ERROR_REASON = "error_reason";
    /**
     * The OPC server host generating the event.
     */
    String OPC_SERVER = "source_server";


}
