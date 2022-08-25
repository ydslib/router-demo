/**
 * Created by : yds
 * Time: 2022-08-25 17:09
 */
package com.crystallake.router.annotation;

import java.util.Map;

import javax.lang.model.element.Element;

public class RouteMeta {

    private RouteType type;         // Type of route
    private Element rawType;        // Raw type of route
    private Class<?> destination;   // Destination
    private String path;            // Path of route
    private String group;           // Group of route
    private int priority = -1;      // The smaller the number, the higher the priority
    private int extra;              // Extra data
    private Map<String, Integer> paramsType;  // Param type
    private String name;

    public RouteMeta() {

    }

    public RouteMeta(Route route, Class<?> destination, RouteType type) {
        this(type, null, destination, null, route.path(), null, null, 0, 0);
    }

    public RouteMeta(Route route, Element rawType, RouteType type, Map<String, Integer> paramsType) {
        this(type, rawType, null, null, route.path(), null, paramsType, 0, 0);
    }

    public RouteMeta(RouteType type, Element rawType, Class<?> destination, String name, String path, String group, Map<String, Integer> paramsType, int priority, int extra) {
        this.type = type;
        this.name = name;
        this.destination = destination;
        this.rawType = rawType;
        this.path = path;
        this.group = group;
        this.paramsType = paramsType;
        this.priority = priority;
        this.extra = extra;
    }

    public static RouteMeta build(RouteType type, Class<?> destination, String path, String group, Map<String, Integer> paramsType, int priority, int extra) {
        return new RouteMeta(type,null,destination,null,path,group,paramsType,priority,extra);
    }


    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public Element getRawType() {
        return rawType;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public Map<String, Integer> getParamsType() {
        return paramsType;
    }

    public void setParamsType(Map<String, Integer> paramsType) {
        this.paramsType = paramsType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
