import SwiftUI
import CoreLocation
import PhotosUI

public struct SubmitComplaintView: View {
    @ObservedObject var viewModel: ComplaintViewModel
    @StateObject private var locationManager = LocationManager.shared
    
    @State private var title: String = ""
    @State private var description: String = ""
    @State private var selectedDepartment: String = "Roads & Potholes"
    @State private var selectedPriority: Priority = .medium
    @State private var locationAddress: String = ""
    @State private var selectedCoordinate: CLLocationCoordinate2D? = nil
    @State private var isAnonymous: Bool = false
    
    @State private var showMapPicker: Bool = false
    @State private var showDistrictPicker: Bool = false
    @State private var showSuccessAlert: Bool = false
    @State private var errorMessage: String? = nil
    
    // Photos
    @State private var selectedPhotoItem: PhotosPickerItem? = nil
    @State private var selectedPhotoData: Data? = nil
    
    public var body: some View {
        NavigationView {
            Form {
                // Section 1: Complaint Details
                Section(header: Text("Complaint Details")) {
                    TextField("Issue Title (e.g. Broken streetlight)", text: $title)
                    
                    Picker("Civic Department", selection: $selectedDepartment) {
                        ForEach(viewModel.departments, id: \.self) { dept in
                            Text(dept).tag(dept)
                        }
                    }
                    
                    Picker("Urgency / Priority", selection: $selectedPriority) {
                        ForEach(Priority.allCases) { pri in
                            Text(pri.rawValue).tag(pri)
                        }
                    }
                    .pickerStyle(SegmentedPickerStyle())
                    
                    ZStack(alignment: .topLeading) {
                        if description.isEmpty {
                            Text("Describe the problem, landmarks, or duration...")
                                .foregroundColor(.secondary.opacity(0.6))
                                .padding(.top, 8)
                        }
                        TextEditor(text: $description)
                            .frame(minHeight: 90)
                    }
                }
                
                // Section 2: Location & Tamil Nadu Districts
                Section(header: Text("Location & Ward in Tamil Nadu")) {
                    // Quick Action Buttons
                    HStack(spacing: 8) {
                        // Live GPS Button
                        Button(action: {
                            locationManager.requestLiveLocation()
                        }) {
                            HStack(spacing: 4) {
                                if locationManager.isDetecting {
                                    ProgressView()
                                        .scaleEffect(0.8)
                                } else {
                                    Image(systemName: "location.fill")
                                }
                                Text("Live GPS")
                                    .fontWeight(.semibold)
                            }
                            .font(.caption)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(Color.green)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                        }
                        .buttonStyle(BorderlessButtonStyle())
                        
                        // Select on Map Button
                        Button(action: {
                            showMapPicker = true
                        }) {
                            HStack(spacing: 4) {
                                Image(systemName: "map.fill")
                                Text("Pick on Map")
                                    .fontWeight(.semibold)
                            }
                            .font(.caption)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                        }
                        .buttonStyle(BorderlessButtonStyle())
                        
                        // 38 Districts Button
                        Button(action: {
                            showDistrictPicker = true
                        }) {
                            HStack(spacing: 4) {
                                Image(systemName: "building.columns.fill")
                                Text("38 Districts")
                                    .fontWeight(.semibold)
                            }
                            .font(.caption)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(Color.purple)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                        }
                        .buttonStyle(BorderlessButtonStyle())
                    }
                    
                    // Address Text Field
                    TextField("Enter exact street or ward", text: $locationAddress)
                    
                    // GPS Coordinates display banner
                    if let coord = selectedCoordinate {
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Image(systemName: "checkmark.seal.fill")
                                    .foregroundColor(.green)
                                Text("Tamil Nadu Location Verified")
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .foregroundColor(.green)
                            }
                            
                            Text("GPS: \(String(format: "%.5f", coord.latitude)), \(String(format: "%.5f", coord.longitude))")
                                .font(.system(size: 11, design: .monospaced))
                                .foregroundColor(.secondary)
                            
                            Button(action: {
                                showMapPicker = true
                            }) {
                                Text("Adjust Pin on Map")
                                    .font(.caption)
                                    .foregroundColor(.blue)
                            }
                        }
                        .padding(.vertical, 4)
                    }
                }
                
                // Section 3: Evidence & Privacy
                Section(header: Text("Photo Evidence & Privacy")) {
                    PhotosPicker(
                        selection: $selectedPhotoItem,
                        matching: .images,
                        photoLibrary: .shared()
                    ) {
                        HStack {
                            Image(systemName: selectedPhotoData != nil ? "photo.fill" : "camera.fill")
                                .foregroundColor(.blue)
                            Text(selectedPhotoData != nil ? "Change Attached Photo" : "Attach Site Photo")
                            Spacer()
                            if selectedPhotoData != nil {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundColor(.green)
                            }
                        }
                    }
                    .onChange(of: selectedPhotoItem) { newItem in
                        Task {
                            if let data = try? await newItem?.loadTransferable(type: Data.self) {
                                selectedPhotoData = data
                            }
                        }
                    }
                    
                    Toggle("Submit Anonymously", isOn: $isAnonymous)
                }
                
                // Submit Button
                Section {
                    Button(action: submitForm) {
                        HStack {
                            Spacer()
                            Image(systemName: "paperplane.fill")
                            Text("Submit Citizen Grievance")
                                .fontWeight(.bold)
                            Spacer()
                        }
                        .padding(.vertical, 6)
                        .foregroundColor(.white)
                    }
                    .listRowBackground(Color.blue)
                }
            }
            .navigationTitle("New Grievance")
            .sheet(isPresented: $showMapPicker) {
                MapPickerSheet(initialCoordinate: selectedCoordinate) { coord, addr in
                    self.selectedCoordinate = coord
                    self.locationAddress = addr
                }
            }
            .sheet(isPresented: $showDistrictPicker) {
                TamilNaduDistrictSelectorView { district, ward in
                    self.selectedCoordinate = district.coordinate
                    if let w = ward {
                        self.locationAddress = "\(w), \(district.name), Tamil Nadu"
                    } else {
                        self.locationAddress = "\(district.popularWards.first ?? "Civic Ward"), \(district.name), Tamil Nadu"
                    }
                }
            }
            .onReceive(locationManager.$resolvedAddress) { addr in
                if !addr.isEmpty && locationAddress.isEmpty {
                    locationAddress = addr
                }
            }
            .onReceive(locationManager.$userLocation) { loc in
                if let l = loc {
                    selectedCoordinate = l
                }
            }
            .alert("Grievance Submitted!", isPresented: $showSuccessAlert) {
                Button("OK") {
                    resetForm()
                }
            } message: {
                Text("Your complaint has been registered with the Tamil Nadu civic authority. A reference ID has been assigned.")
            }
        }
    }
    
    private func submitForm() {
        guard !title.trimmingCharacters(in: .whitespaces).isEmpty else {
            return
        }
        
        let finalLocation = locationAddress.isEmpty ? "Anna Salai, Chennai, Tamil Nadu" : locationAddress
        let finalLat = selectedCoordinate?.latitude ?? 13.0827
        let finalLng = selectedCoordinate?.longitude ?? 80.2707
        
        viewModel.addComplaint(
            title: title,
            description: description.isEmpty ? "Reported civic issue requiring municipal attention." : description,
            department: selectedDepartment,
            location: finalLocation,
            priority: selectedPriority,
            latitude: finalLat,
            longitude: finalLng,
            isAnonymous: isAnonymous
        )
        
        showSuccessAlert = true
    }
    
    private func resetForm() {
        title = ""
        description = ""
        locationAddress = ""
        selectedCoordinate = nil
        selectedPhotoData = nil
        selectedPhotoItem = nil
    }
}
