package com.ratnesh.buildly.entity;

import com.ratnesh.buildly.enums.PreviewStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
//@Entity

public class Preview {
    Long id;
//    Project project;
    String nameSpace;
    String podName;
    String previewUrl;
    PreviewStatus status;
    Instant startedAT;
    Instant terminatedAt;
    Instant createdAt;
}
