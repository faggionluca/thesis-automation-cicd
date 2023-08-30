package com.lucafaggion.thesis.develop.model.mixins;

import org.springframework.boot.jackson.JsonMixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dockerjava.api.model.BindOptions;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.api.model.MountType;
import com.github.dockerjava.api.model.TmpfsOptions;
import com.github.dockerjava.api.model.VolumeOptions;

@JsonMixin(Mount.class)
public abstract class DockerAPIMountMixin {
    /**
     * @since 1.24
     */
    @JsonProperty("Type")
    private MountType type;

    /**
     * @since 1.24
     */
    @JsonProperty("Source")
    private String source;

    /**
     * @since 1.24
     */
    @JsonProperty("Target")
    private String target;

    /**
     * @since 1.24
     */
    @JsonProperty("ReadOnly")
    private Boolean readOnly;

    /**
     * @since 1.24
     */
    @JsonProperty("BindOptions")
    private BindOptions bindOptions;

    /**
     * @since 1.24
     */
    @JsonProperty("VolumeOptions")
    private VolumeOptions volumeOptions;

    /**
     * @since 1.29
     */
    @JsonProperty("TmpfsOptions")
    private TmpfsOptions tmpfsOptions;
}
