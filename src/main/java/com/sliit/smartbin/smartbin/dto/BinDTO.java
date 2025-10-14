package com.sliit.smartbin.smartbin.dto;

import com.sliit.smartbin.smartbin.model.Bin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinDTO {
    private Long id;
    private String qrCode;
    private String location;
    private Double latitude;
    private Double longitude;
    private Bin.BinType binType;
    private Bin.BinStatus status;
    private Integer fillLevel;
    private Boolean alertFlag;
}

