/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.ozone.ksm.helpers;


import com.google.common.base.Preconditions;
import org.apache.hadoop.ozone.protocol.proto.KeySpaceManagerProtocolProtos;
import org.apache.hadoop.ozone.protocol.proto.KeySpaceManagerProtocolProtos
    .ServicePort;
import org.apache.hadoop.ozone.protocol.proto.OzoneProtos.NodeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ServiceInfo holds the config details of Ozone services.
 */
public final class ServiceInfo {

  /**
   * Type of node/service.
   */
  private final NodeType nodeType;
  /**
   * Hostname of the node in which the service is running.
   */
  private final String hostname;

  /**
   * List of ports the service listens to.
   */
  private final Map<ServicePort.Type, ServicePort> portsMap;

  /**
   * Constructs the ServiceInfo for the {@code nodeType}.
   * @param nodeType type of node/service
   * @param hostname hostname of the service
   * @param ports list of ports the service listens to
   */
  private ServiceInfo(
      NodeType nodeType, String hostname, List<ServicePort> ports) {
    Preconditions.checkNotNull(nodeType);
    Preconditions.checkNotNull(hostname);
    this.nodeType = nodeType;
    this.hostname = hostname;
    this.portsMap = new HashMap<>();
    for (ServicePort port : ports) {
      portsMap.put(port.getType(), port);
    }
  }

  /**
   * Returns the type of node/service.
   * @return node type
   */
  public NodeType getNodeType() {
    return nodeType;
  }

  /**
   * Returns the hostname of the service.
   * @return hostname
   */
  public String getHostname() {
    return hostname;
  }

  /**
   * Returns the list of port which the service listens to.
   * @return List<ServicePort>
   */
  public List<ServicePort> getPorts() {
    return portsMap.values().parallelStream().collect(Collectors.toList());
  }

  /**
   * Returns the port for given type, null if the service doesn't support
   * the type.
   *
   * @param type the type of port.
   *             ex: RPC, HTTP, HTTPS, etc..
   */
  public int getPort(ServicePort.Type type) {
    return portsMap.get(type).getValue();
  }

  /**
   * Converts {@link ServiceInfo} to KeySpaceManagerProtocolProtos.ServiceInfo.
   *
   * @return KeySpaceManagerProtocolProtos.ServiceInfo
   */
  public KeySpaceManagerProtocolProtos.ServiceInfo getProtobuf() {
    KeySpaceManagerProtocolProtos.ServiceInfo.Builder builder =
        KeySpaceManagerProtocolProtos.ServiceInfo.newBuilder();
    builder.setNodeType(nodeType)
        .setHostname(hostname)
        .addAllServicePorts(portsMap.values());
    return builder.build();
  }

  /**
   * Converts KeySpaceManagerProtocolProtos.ServiceInfo to {@link ServiceInfo}.
   *
   * @return {@link ServiceInfo}
   */
  public static ServiceInfo getFromProtobuf(
      KeySpaceManagerProtocolProtos.ServiceInfo serviceInfo) {
    return new ServiceInfo(serviceInfo.getNodeType(),
        serviceInfo.getHostname(),
        serviceInfo.getServicePortsList());
  }


  /**
   * Creates a new builder to build {@link ServiceInfo}.
   * @return {@link ServiceInfo.Builder}
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builder used to build/construct {@link ServiceInfo}.
   */
  public static class Builder {

    private NodeType node;
    private String host;
    private List<ServicePort> ports = new ArrayList<>();


    /**
     * Sets the node/service type.
     * @param nodeType type of node
     * @return the builder
     */
    public Builder setNodeType(NodeType nodeType) {
      node = nodeType;
      return this;
    }

    /**
     * Sets the hostname of the service.
     * @param hostname service hostname
     * @return the builder
     */
    public Builder setHostname(String hostname) {
      host = hostname;
      return this;
    }

    /**
     * Adds the service port to the service port list.
     * @param servicePort RPC port
     * @return the builder
     */
    public Builder addServicePort(ServicePort servicePort) {
      ports.add(servicePort);
      return this;
    }


    /**
     * Builds and returns {@link ServiceInfo} with the set values.
     * @return {@link ServiceInfo}
     */
    public ServiceInfo build() {
      return new ServiceInfo(node, host, ports);
    }
  }

}