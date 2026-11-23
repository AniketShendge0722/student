package student.mgt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "routes",
        indexes = {
                @Index(name = "idx_route_name", columnList = "route_name")
        }
)
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_name", nullable = false, length = 100)
    private String routeName;

    @Column(name = "start_point", nullable = false, length = 150)
    private String startPoint;

    @Column(name = "end_point", nullable = false, length = 150)
    private String endPoint;

    @Column(name = "stops", nullable = false, length = 1000)
    private String stops;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @OneToMany(
            mappedBy = "route",
            fetch = FetchType.LAZY
    )
    private List<Bus> buses = new ArrayList<>();

    public Route() {
    }

    public Route(Long id, String routeName, String startPoint,
                 String endPoint, String stops, Double distanceKm) {

        this.id = id;
        this.routeName = routeName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.stops = stops;
        this.distanceKm = distanceKm;
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

    public List<Bus> getBuses() {
        return buses;
    }

    public void setBuses(List<Bus> buses) {
        this.buses = buses;
    }
}