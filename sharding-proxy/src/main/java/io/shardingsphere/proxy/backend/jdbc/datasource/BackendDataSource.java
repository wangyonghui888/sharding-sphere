/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.proxy.backend.jdbc.datasource;

import io.shardingsphere.core.constant.TransactionType;
import io.shardingsphere.core.rule.DataSourceParameter;
import lombok.Getter;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Backend data source factory.
 *
 * @author zhaojun
 * @author zhangliang
 */
@Getter
public final class BackendDataSource {
    
    private final Map<String, DataSource> dataSourceMap;
    
    public BackendDataSource(final TransactionType transactionType, final Map<String, DataSourceParameter> dataSourceParameters) {
        dataSourceMap = createDataSourceMap(transactionType, dataSourceParameters);
    }
    
    private Map<String, DataSource> createDataSourceMap(final TransactionType transactionType, final Map<String, DataSourceParameter> dataSourceParameters) {
        Map<String, DataSource> result = new LinkedHashMap<>(dataSourceParameters.size());
        for (Entry<String, DataSourceParameter> entry : dataSourceParameters.entrySet()) {
            result.put(entry.getKey(), getBackendDataSourceFactory(transactionType).build(entry.getKey(), entry.getValue()));
        }
        return result;
    }
    
    private BackendDataSourceFactory getBackendDataSourceFactory(final TransactionType transactionType) {
        switch (transactionType) {
            case XA:
                return new XABackendDataSourceFactory();
            default:
                return new RawBackendDataSourceFactory();
        }
    }
}