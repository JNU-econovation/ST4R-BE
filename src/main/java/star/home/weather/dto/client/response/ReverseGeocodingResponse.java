package star.home.weather.dto.client.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReverseGeocodingResponse(ResponseData response) {

    public record ResponseData(
            ServiceData service,
            String status,
            InputData input,
            List<ResultData> result
    ) {}

    public record ServiceData(
            String name,
            String version,
            String operation,
            String time
    ) {}

    public record InputData(
            PointData point,
            String crs,
            String type
    ) {}

    public record PointData(
            String x,
            String y
    ) {}

    public record ResultData(
            String type,
            String text,
            StructureData structure
    ) {}

    public record StructureData(
            String level0,
            String level1,
            String level2,
            String level3,
            String level4L,
            String level4LC,
            String level4A,
            String level4AC,
            String level5,
            String detail
    ) {}
}
