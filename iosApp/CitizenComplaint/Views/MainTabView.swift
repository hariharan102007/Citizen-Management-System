import SwiftUI

public struct MainTabView: View {
    @StateObject private var viewModel = ComplaintViewModel()
    
    public init() {}
    
    public var body: some View {
        TabView {
            ComplaintsListView(viewModel: viewModel)
                .tabItem {
                    Label("Complaints", systemImage: "list.bullet.rectangle.portrait.fill")
                }
            
            ComplaintMapView(viewModel: viewModel)
                .tabItem {
                    Label("TN Civic Map", systemImage: "map.fill")
                }
            
            SubmitComplaintView(viewModel: viewModel)
                .tabItem {
                    Label("Report Issue", systemImage: "plus.circle.fill")
                }
            
            iOSInfoView()
                .tabItem {
                    Label("iOS Info", systemImage: "iphone")
                }
        }
        .accentColor(.blue)
    }
}

public struct iOSInfoView: View {
    public var body: some View {
        NavigationView {
            List {
                Section(header: Text("Native iOS Architecture")) {
                    HStack {
                        Image(systemName: "applelogo")
                            .font(.title2)
                            .foregroundColor(.primary)
                        VStack(alignment: .leading) {
                            Text("SwiftUI & MapKit Native")
                                .font(.headline)
                            Text("Apple iOS 17+ Optimized")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    .padding(.vertical, 4)
                }
                
                Section(header: Text("Key Modules Implemented")) {
                    Label("38 Tamil Nadu Administrative Districts", systemImage: "building.columns")
                    Label("Apple MapKit Interactive Pin Placement", systemImage: "mappin.and.ellipse")
                    Label("CoreLocation GPS & Reverse Geocoding", systemImage: "location.fill")
                    Label("Civic Department & Priority Triage", systemImage: "flame.fill")
                    Label("Anonymous & Photo Evidence Attachment", systemImage: "camera.fill")
                }
                
                Section(header: Text("How to Build for your iPhone")) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("1. In Google AI Studio, click 'Export to ZIP' or 'Push to GitHub'.")
                        Text("2. Open the 'iosApp' folder on any Mac in Xcode.")
                        Text("3. Connect your iPhone via USB, select your Apple ID in Signing, and click Run (Cmd+R).")
                    }
                    .font(.footnote)
                    .foregroundColor(.secondary)
                }
            }
            .listStyle(InsetGroupedListStyle())
            .navigationTitle("iOS Application Info")
        }
    }
}
