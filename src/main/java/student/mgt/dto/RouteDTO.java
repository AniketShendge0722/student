package student.mgt.dto;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class RouteDTO {

    private Long id;

    @NotBlank(message = "Route name is required")
    @Size(min = 2, max = 100)
    private String routeName;

    @NotBlank(message = "Start point is required")
    @Size(max = 150)
    private String startPoint;

    @NotBlank(message = "End point is required")
    @Size(max = 150)
    private String endPoint;

    @NotBlank(message = "Please enter route stops")
    @Size(max = 1000)
    private String stops;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.1", message = "Distance must be greater than zero")
    @DecimalMax(value = "1000.0", message = "Distance cannot exceed 1000 KM")
    private Double distanceKm;

    private List<Long> busIds = new ArrayList<>();

    public RouteDTO() {
    }

    public RouteDTO(Long id, String routeName, String startPoint,
                    String endPoint, String stops, Double distanceKm,
                    List<Long> busIds) {
        this.id = id;
        this.routeName = routeName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.stops = stops;
        this.distanceKm = distanceKm;
        this.busIds = busIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getStops() {
        return stops;
    }

    public void setStops(String stops) {
        this.stops = stops;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public List<Long> getBusIds() {
        return busIds;
    }

    public void setBusIds(List<Long> busIds) {
        this.busIds = busIds;
    }
}