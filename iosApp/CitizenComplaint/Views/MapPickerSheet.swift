import SwiftUI
import MapKit

public struct MapPickerSheet: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var locationManager = LocationManager.shared
    
    @State private var position: MapCameraPosition
    @State private var selectedCoordinate: CLLocationCoordinate2D
    @State private var selectedDistrict: TNDistrict
    @State private var resolvedAddress: String = ""
    @State private var showDistrictSheet = false
    
    public var initialCoordinate: CLLocationCoordinate2D?
    public var onLocationConfirmed: (CLLocationCoordinate2D, String) -> Void
    
    public init(
        initialCoordinate: CLLocationCoordinate2D? = nil,
        onLocationConfirmed: @escaping (CLLocationCoordinate2D, String) -> Void
    ) {
        self.initialCoordinate = initialCoordinate
        self.onLocationConfirmed = onLocationConfirmed
        
        let startCoord = initialCoordinate ?? CLLocationCoordinate2D(latitude: 13.0827, longitude: 80.2707) // Default Chennai
        _selectedCoordinate = State(initialValue: startCoord)
        _position = State(initialValue: .region(MKCoordinateRegion(
            center: startCoord,
            span: MKCoordinateSpan(latitudeDelta: 0.08, longitudeDelta: 0.08)
        )))
        _selectedDistrict = State(initialValue: TamilNaduData.nearestDistrict(to: startCoord))
    }
    
    public var body: some View {
        NavigationView {
            ZStack {
                // Interactive Apple Map
                MapReader { proxy in
                    Map(position: $position) {
                        Annotation("Selected Location", coordinate: selectedCoordinate) {
                            VStack(spacing: 2) {
                                Image(systemName: "mappin.circle.fill")
                                    .font(.system(size: 36))
                                    .foregroundColor(.red)
                                    .shadow(radius: 3)
                                
                                Text("Tapped Pin")
                                    .font(.system(size: 10, weight: .bold))
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(Color.white.opacity(0.9))
                                    .cornerRadius(4)
                                    .shadow(radius: 1)
                            }
                        }
                    }
                    .onTapGesture { screenPoint in
                        if let coordinate = proxy.convert(screenPoint, from: .local) {
                            updateSelectedLocation(coordinate)
                        }
                    }
                }
                .ignoresSafeArea(edges: .bottom)
                
                // Top Overlay: Quick District Chips
                VStack {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            Button(action: {
                                showDistrictSheet = true
                            }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "line.3.horizontal.decrease.circle.fill")
                                    Text("All 38 Districts")
                                }
                                .font(.caption)
                                .fontWeight(.semibold)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 8)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(20)
                                .shadow(radius: 2)
                            }
                            
                            ForEach(["Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli"], id: \.self) { name in
                                if let dist = TamilNaduData.all38Districts.first(where: { $0.name == name }) {
                                    Button(action: {
                                        jumpToDistrict(dist)
                                    }) {
                                        Text(dist.name)
                                            .font(.caption)
                                            .fontWeight(.medium)
                                            .padding(.horizontal, 12)
                                            .padding(.vertical, 8)
                                            .background(selectedDistrict.name == dist.name ? Color.black : Color(UIColor.systemBackground))
                                            .foregroundColor(selectedDistrict.name == dist.name ? .white : .primary)
                                            .cornerRadius(20)
                                            .shadow(radius: 2)
                                    }
                                }
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                    }
                    
                    Spacer()
                }
                
                // Floating Action Controls (Right side)
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        VStack(spacing: 12) {
                            // Live GPS Button
                            Button(action: {
                                locationManager.requestLiveLocation()
                            }) {
                                Image(systemName: "location.fill")
                                    .font(.title3)
                                    .foregroundColor(.white)
                                    .frame(width: 48, height: 48)
                                    .background(Color.green)
                                    .clipShape(Circle())
                                    .shadow(radius: 4)
                            }
                        }
                        .padding(.trailing, 16)
                        .padding(.bottom, 160)
                    }
                }
                
                // Bottom Confirmation Card
                VStack {
                    Spacer()
                    VStack(alignment: .leading, spacing: 10) {
                        HStack(alignment: .top) {
                            Image(systemName: "mappin.and.ellipse")
                                .font(.title3)
                                .foregroundColor(.red)
                            
                            VStack(alignment: .leading, spacing: 3) {
                                Text(selectedDistrict.name + ", Tamil Nadu")
                                    .font(.headline)
                                
                                Text(resolvedAddress.isEmpty ? "Tap anywhere on the map to position pin" : resolvedAddress)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .lineLimit(2)
                                
                                Text("Lat: \(String(format: "%.5f", selectedCoordinate.latitude)), Lng: \(String(format: "%.5f", selectedCoordinate.longitude))")
                                    .font(.system(size: 11, weight: .regular, design: .monospaced))
                                    .foregroundColor(.secondary)
                            }
                            
                            Spacer()
                        }
                        
                        Button(action: {
                            let finalAddress = resolvedAddress.isEmpty ?
                                "\(selectedDistrict.popularWards.first ?? "Ward"), \(selectedDistrict.name), Tamil Nadu" : resolvedAddress
                            onLocationConfirmed(selectedCoordinate, finalAddress)
                            dismiss()
                        }) {
                            HStack {
                                Image(systemName: "checkmark.circle.fill")
                                Text("Confirm This Location")
                                    .fontWeight(.bold)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                            .shadow(radius: 2)
                        }
                    }
                    .padding(16)
                    .background(
                        RoundedRectangle(cornerRadius: 16)
                            .fill(Color(UIColor.systemBackground))
                            .shadow(color: .black.opacity(0.15), radius: 8, y: -2)
                    )
                    .padding(.horizontal, 12)
                    .padding(.bottom, 8)
                }
            }
            .navigationTitle("Select Location on Map")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
            .sheet(isPresented: $showDistrictSheet) {
                TamilNaduDistrictSelectorView { district, ward in
                    jumpToDistrict(district, ward: ward)
                }
            }
            .onReceive(locationManager.$userLocation) { newLoc in
                if let loc = newLoc {
                    updateSelectedLocation(loc)
                    position = .region(MKCoordinateRegion(
                        center: loc,
                        span: MKCoordinateSpan(latitudeDelta: 0.04, longitudeDelta: 0.04)
                    ))
                }
            }
            .onAppear {
                updateSelectedLocation(selectedCoordinate)
            }
        }
    }
    
    private func updateSelectedLocation(_ coordinate: CLLocationCoordinate2D) {
        selectedCoordinate = coordinate
        let dist = TamilNaduData.nearestDistrict(to: coordinate)
        selectedDistrict = dist
        locationManager.processCoordinate(coordinate)
        
        let geocoder = CLGeocoder()
        let clLoc = CLLocation(latitude: coordinate.latitude, longitude: coordinate.longitude)
        geocoder.reverseGeocodeLocation(clLoc) { placemarks, _ in
            if let mark = placemarks?.first {
                var items: [String] = []
                if let street = mark.thoroughfare { items.append(street) }
                if let area = mark.subLocality { items.append(area) }
                if let city = mark.locality { items.append(city) }
                if !items.isEmpty {
                    self.resolvedAddress = items.joined(separator: ", ") + ", \(dist.name), TN"
                    return
                }
            }
            let ward = dist.popularWards.first ?? "Civic Ward"
            self.resolvedAddress = "\(ward), \(dist.name), Tamil Nadu"
        }
    }
    
    private func jumpToDistrict(_ district: TNDistrict, ward: String? = nil) {
        selectedDistrict = district
        selectedCoordinate = district.coordinate
        position = .region(MKCoordinateRegion(
            center: district.coordinate,
            span: MKCoordinateSpan(latitudeDelta: 0.06, longitudeDelta: 0.06)
        ))
        if let w = ward {
            resolvedAddress = "\(w), \(district.name), Tamil Nadu"
        } else {
            resolvedAddress = "\(district.popularWards.first ?? "District Center"), \(district.name), Tamil Nadu"
        }
    }
}
