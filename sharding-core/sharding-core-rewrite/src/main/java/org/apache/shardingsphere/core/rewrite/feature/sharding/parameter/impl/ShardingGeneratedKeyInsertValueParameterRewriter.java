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

package org.apache.shardingsphere.core.rewrite.feature.sharding.parameter.impl;

import lombok.Setter;
import org.apache.shardingsphere.core.preprocessor.statement.SQLStatementContext;
import org.apache.shardingsphere.core.preprocessor.statement.impl.InsertSQLStatementContext;
import org.apache.shardingsphere.core.rewrite.parameter.builder.ParameterBuilder;
import org.apache.shardingsphere.core.rewrite.parameter.builder.impl.GroupedParameterBuilder;
import org.apache.shardingsphere.core.rewrite.parameter.rewriter.ParameterRewriter;
import org.apache.shardingsphere.core.rewrite.feature.sharding.token.generator.SQLRouteResultAware;
import org.apache.shardingsphere.core.route.SQLRouteResult;

import java.util.Iterator;
import java.util.List;

/**
 * Sharding generated key insert value parameter rewriter.
 *
 * @author zhangliang
 */
@Setter
public final class ShardingGeneratedKeyInsertValueParameterRewriter implements ParameterRewriter, SQLRouteResultAware {
    
    private SQLRouteResult sqlRouteResult;
    
    @Override
    public void rewrite(final ParameterBuilder parameterBuilder, final SQLStatementContext sqlStatementContext, final List<Object> parameters) {
        if (sqlStatementContext instanceof InsertSQLStatementContext && sqlRouteResult.getGeneratedKey().isPresent() && sqlRouteResult.getGeneratedKey().get().isGenerated()) {
            Iterator<Comparable<?>> generatedValues = sqlRouteResult.getGeneratedKey().get().getGeneratedValues().descendingIterator();
            int count = 0;
            for (List<Object> each : ((InsertSQLStatementContext) sqlStatementContext).getGroupedParameters()) {
                Comparable<?> generatedValue = generatedValues.next();
                if (!each.isEmpty()) {
                    ((GroupedParameterBuilder) parameterBuilder).getParameterBuilders().get(count).getAddedIndexAndParameters().put(each.size(), generatedValue);
                }
                count++;
            }
        }
    }
}
