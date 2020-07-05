package com.yuxin.esapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.common.geo.GeoPoint;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyGeo {

    private String name;
    private MYPoint location;

}