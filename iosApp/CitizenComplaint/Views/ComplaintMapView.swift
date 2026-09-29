import SwiftUI
import MapKit

public struct ComplaintMapView: View {
    @ObservedObject var viewModel: ComplaintViewModel
    @StateObject private var locationManager = LocationManager.shared
    
    @State private var position: MapCameraPosition = .region(MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 11.1271, longitude: 78.6569), // Central Tamil Nadu
        span: MKCoordinateSpan(latitudeDelta: 4.0, longitudeDelta: 4.0)
    ))
    
    @State private var selectedComplaint: Complaint? = nil
    @State private var showDistrictSheet: Bool = false
    
    public var body: some View {
        NavigationView {
            ZStack {
                Map(position: $position) {
                    ForEach(viewModel.complaints) { complaint in
                        Annotation(complaint.title, coordinate: complaint.coordinate) {
                            Button(action: {
                                selectedComplaint = complaint
                            }) {
                                VStack(spacing: 2) {
                                    Image(systemName: "exclamationmark.bubble.fill")
                                        .font(.title2)
                                        .foregroundColor(pinColor(complaint.priority))
                                        .background(Circle().fill(Color.white).frame(width: 28, height: 28))
                                        .shadow(radius: 3)
                                }
                            }
                        }
                    }
                }
                .ignoresSafeArea(edges: .bottom)
                
                // Top district bar
                VStack {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            Button(action: {
                                showDistrictSheet = true
                            }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "building.columns.fill")
                                    Text("Jump to District")
                                }
                                .font(.caption)
                                .fontWeight(.bold)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 8)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(20)
                                .shadow(radius: 2)
                            }
                            
                            ForEach(["Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem"], id: \.self) { name in
                                if let dist = TamilNaduData.all38Districts.first(where: { $0.name == name }) {
                                    Button(action: {
                                        position = .region(MKCoordinateRegion(
                                            center: dist.coordinate,
                                            span: MKCoordinateSpan(latitudeDelta: 0.15, longitudeDelta: 0.15)
                                        ))
                                    }) {
                                        Text(dist.name)
                                            .font(.caption)
                                            .padding(.horizontal, 12)
                                            .padding(.vertical, 8)
                                            .background(Color(UIColor.systemBackground))
                                            .foregroundColor(.primary)
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
                
                // Floating Live GPS button
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        Button(action: {
                            locationManager.requestLiveLocation()
                        }) {
                            Image(systemName: "location.fill")
                                .font(.title3)
                                .foregroundColor(.white)
                                .frame(width: 50, height: 50)
                                .background(Color.green)
                                .clipShape(Circle())
                                .shadow(radius: 4)
                        }
                        .padding(.trailing, 16)
                        .padding(.bottom, selectedComplaint != nil ? 180 : 30)
                    }
                }
                
                // Selected Complaint Bottom Card
                if let c = selectedComplaint {
                    VStack {
                        Spacer()
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text(c.categoryName)
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 3)
                                    .background(Color.blue.opacity(0.12))
                                    .foregroundColor(.blue)
                                    .cornerRadius(6)
                                
                                Spacer()
                                
                                Button(action: {
                                    selectedComplaint = nil
                                }) {
                                    Image(systemName: "xmark.circle.fill")
                                        .foregroundColor(.secondary)
                                }
                            }
                            
                            Text(c.title)
                                .font(.headline)
                            
                            Text(c.location)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            
                            HStack {
                                Text("Status: \(c.status.rawValue)")
                                    .font(.caption)
                                    .fontWeight(.semibold)
                                
                                Spacer()
                                
                                Text("\(c.supportCount) Upvotes")
                                    .font(.caption)
                                    .foregroundColor(.blue)
                            }
                        }
                        .padding(16)
                        .background(
                            RoundedRectangle(cornerRadius: 16)
                                .fill(Color(UIColor.systemBackground))
                                .shadow(color: .black.opacity(0.15), radius: 8, y: -2)
                        )
                        .padding(.horizontal, 16)
                        .padding(.bottom, 16)
                    }
                }
            }
            .navigationTitle("Tamil Nadu Live Civic Map")
            .navigationBarTitleDisplayMode(.inline)
            .sheet(isPresented: $showDistrictSheet) {
                TamilNaduDistrictSelectorView { district, _ in
                    position = .region(MKCoordinateRegion(
                        center: district.coordinate,
                        span: MKCoordinateSpan(latitudeDelta: 0.15, longitudeDelta: 0.15)
                    ))
                }
            }
            .onReceive(locationManager.$userLocation) { loc in
                if let l = loc {
                    position = .region(MKCoordinateRegion(
                        center: l,
                        span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05)
                    ))
                }
            }
        }
    }
    
    private func pinColor(_ priority: Priority) -> Color {
        switch priority {
        case .low: return .gray
        case .medium: return .blue
        case .high: return .orange
        case .emergency: return .red
        }
    }
}
