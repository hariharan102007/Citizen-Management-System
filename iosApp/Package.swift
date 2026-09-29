// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CitizenComplaint",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v17)
    ],
    products: [
        .library(
            name: "CitizenComplaint",
            targets: ["CitizenComplaint"]
        )
    ],
    targets: [
        .target(
            name: "CitizenComplaint",
            path: "CitizenComplaint"
        )
    ]
)
