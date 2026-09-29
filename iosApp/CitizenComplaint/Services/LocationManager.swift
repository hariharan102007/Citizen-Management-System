import Foundation
import CoreLocation
import Combine

public class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    public static let shared = LocationManager()
    
    private let manager = CLLocationManager()
    private let geocoder = CLGeocoder()
    
    @Published public var userLocation: CLLocationCoordinate2D?
    @Published public var resolvedAddress: String = ""
    @Published public var detectedDistrict: TNDistrict?
    @Published public var isDetecting: Bool = false
    @Published public var authorizationStatus: CLAuthorizationStatus = .notDetermined
    @Published public var errorMessage: String?
    
    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
        authorizationStatus = manager.authorizationStatus
    }
    
    public func requestLiveLocation() {
        isDetecting = true
        errorMessage = nil
        
        switch manager.authorizationStatus {
        case .notDetermined:
            manager.requestWhenInUseAuthorization()
        case .restricted, .denied:
            isDetecting = false
            errorMessage = "Location access denied. Please enable in iPhone Settings > Privacy > Location."
            // Fallback to Chennai
            selectFallbackDistrict(districtName: "Chennai")
        case .authorizedWhenInUse, .authorizedAlways:
            manager.requestLocation()
        @unknown default:
            manager.requestLocation()
        }
    }
    
    public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        self.authorizationStatus = manager.authorizationStatus
        if authorizationStatus == .authorizedWhenInUse || authorizationStatus == .authorizedAlways {
            if isDetecting {
                manager.requestLocation()
            }
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        DispatchQueue.main.async {
            self.userLocation = location.coordinate
            self.processCoordinate(location.coordinate)
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        DispatchQueue.main.async {
            self.isDetecting = false
            self.errorMessage = "Unable to fetch GPS: \(error.localizedDescription)"
            // Fallback default for simulator
            if self.userLocation == nil {
                self.selectFallbackDistrict(districtName: "Chennai")
            }
        }
    }
    
    public func processCoordinate(_ coordinate: CLLocationCoordinate2D) {
        isDetecting = true
        let clLocation = CLLocation(latitude: coordinate.latitude, longitude: coordinate.longitude)
        
        // Find nearest Tamil Nadu District
        let nearest = TamilNaduData.nearestDistrict(to: coordinate)
        self.detectedDistrict = nearest
        
        geocoder.reverseGeocodeLocation(clLocation) { [weak self] placemarks, error in
            DispatchQueue.main.async {
                self?.isDetecting = false
                if let mark = placemarks?.first {
                    var components: [String] = []
                    if let name = mark.name, !name.isEmpty { components.append(name) }
                    if let subLocality = mark.subLocality, !subLocality.isEmpty { components.append(subLocality) }
                    if let locality = mark.locality, !locality.isEmpty { components.append(locality) }
                    if let postal = mark.postalCode, !postal.isEmpty { components.append(postal) }
                    
                    let addr = components.joined(separator: ", ")
                    if !addr.isEmpty {
                        self?.resolvedAddress = "\(addr), \(nearest.name), Tamil Nadu"
                        return
                    }
                }
                
                // Fallback formatted text
                let ward = nearest.popularWards.first ?? "Main Ward"
                self?.resolvedAddress = "\(ward), \(nearest.name), Tamil Nadu (Lat: \(String(format: "%.4f", coordinate.latitude)), Lng: \(String(format: "%.4f", coordinate.longitude)))"
            }
        }
    }
    
    public func selectFallbackDistrict(districtName: String) {
        if let district = TamilNaduData.all38Districts.first(where: { $0.name.lowercased() == districtName.lowercased() }) {
            self.detectedDistrict = district
            self.userLocation = district.coordinate
            let ward = district.popularWards.first ?? "Civic Ward"
            self.resolvedAddress = "\(ward), \(district.name), Tamil Nadu"
        }
    }
}
