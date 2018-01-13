/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.gcp.bigquery;

import org.apache.nifi.processors.gcp.storage.*;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.JobInfo;
import org.apache.nifi.util.TestRunner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.reset;

/**
 * Unit tests for {@link PutBigQueryStream}.
 */
public class PutBigQueryStreamTest extends AbstractBQTest {
    private static final String TABLENAME = "test_table";
    private static final String TABLE_SCHEMA = "[{ \"mode\": \"NULLABLE\", \"name\": \"data\", \"type\": \"STRING\" }]";
    private static final String BATCH_SIZE = "100";
    private static final String MAX_ROW_SIZE = "2 MB";
    private static final String CREATE_DISPOSITION = JobInfo.CreateDisposition.CREATE_IF_NEEDED.name();
    private static final String TABLE_CACHE_RESET = "1 hours";
    
    @Mock
    BigQuery bq;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Override
    public AbstractBigQueryProcessor getProcessor() {
        return new PutBigQueryStream() {
            @Override
            protected BigQuery getCloudService() {
                return bq;
            }
        };
    }

    @Override
    protected void addRequiredPropertiesToRunner(TestRunner runner) {
        runner.setProperty(PutBigQueryStream.DATASET, DATASET);
        runner.setProperty(PutBigQueryStream.TABLE_NAME, TABLENAME);
        runner.setProperty(PutBigQueryStream.TABLE_SCHEMA, TABLE_SCHEMA);
        runner.setProperty(PutBigQueryStream.BATCH_SIZE, BATCH_SIZE);
        runner.setProperty(PutBigQueryStream.MAX_ROW_SIZE, MAX_ROW_SIZE);
        runner.setProperty(PutBigQueryStream.CREATE_DISPOSITION, CREATE_DISPOSITION);
        runner.setProperty(PutBigQueryStream.TABLE_CACHE_RESET, TABLE_CACHE_RESET);
    }

    @Test
    public void testSuccessfulInsert() throws Exception {
        reset(bq);
        final TestRunner runner = buildNewRunner(getProcessor());
        addRequiredPropertiesToRunner(runner);
        runner.assertValid();
        
        runner.enqueue("{ \"data\": \"datavalue\" }");
        
        runner.run();
    }
}
