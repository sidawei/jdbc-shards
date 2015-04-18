/*
 * Copyright 2015 suning.com Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Created on 2015年4月15日
// $Id$

package com.suning.snfddal.shards;

import java.util.List;

/**
 * @author <a href="mailto:jorgie.mail@gmail.com">jorgie li</a>
 *
 */
public class StandaloneSelector implements DataSourceSelector {
    
    private String shardName;

    private DataSourceMarker standalone;
    
    public StandaloneSelector(String shardName, DataSourceMarker standalone) {
        super();
        this.shardName = shardName;
        this.standalone = standalone;
    }

    @Override
    public DataSourceMarker doSelect(Optional option) {
        return standalone;
    }

    @Override
    public String getShardName() {
        return shardName;
    }


    @Override
    public void doHandleAbnormal(DataSourceMarker source) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void doHandleWakeup(DataSourceMarker source) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.suning.snfddal.shards.DataSourceSelector#doSelect(com.suning.snfddal.shards.Optional, java.util.List)
     */
    @Override
    public DataSourceMarker doSelect(Optional option, List<DataSourceMarker> exclusive) {
        // TODO Auto-generated method stub
        return null;
    }

}