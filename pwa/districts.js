const TN_REGIONS = {
  ALL: "All 38 Districts",
  NORTHERN: "Northern TN",
  WESTERN: "Western / Kongu",
  CENTRAL: "Central & Delta",
  SOUTHERN: "Southern TN"
};

const ALL_38_DISTRICTS = [
  // Northern Region
  { name: "Chennai", lat: 13.0827, lng: 80.2707, zone: "Greater Chennai Corp", wards: ["Anna Salai / T. Nagar", "Marina Beach", "Velachery", "Adyar", "Guindy", "Mylapore", "Tambaram"], region: "Northern TN" },
  { name: "Chengalpattu", lat: 12.6841, lng: 79.9836, zone: "Chengalpattu Municipality", wards: ["GST Road", "Mahabalipuram Coast", "Maraimalai Nagar", "Singaperumal Koil"], region: "Northern TN" },
  { name: "Kanchipuram", lat: 12.8342, lng: 79.7036, zone: "Kanchipuram Corp", wards: ["Temple City Ward", "Gandhi Road", "Orikkai", "Kaveripakkam Rd"], region: "Northern TN" },
  { name: "Tiruvallur", lat: 13.1231, lng: 79.9120, zone: "Tiruvallur Municipality", wards: ["Veeraraghava Zone", "Avadi", "Poonamallee", "Gummidipoondi"], region: "Northern TN" },
  { name: "Vellore", lat: 12.9165, lng: 79.1325, zone: "Vellore Corp", wards: ["Fort City", "Katpadi", "Bagayam", "Sathuvachari"], region: "Northern TN" },
  { name: "Ranipet", lat: 12.9272, lng: 79.3330, zone: "Ranipet Municipality", wards: ["SIPCOT Zone", "Arcot", "Walajapet", "Arakkonam"], region: "Northern TN" },
  { name: "Tirupathur", lat: 12.4996, lng: 78.5739, zone: "Tirupathur Municipality", wards: ["Yelagiri Hills Zone", "Vaniyambadi", "Ambur Leather Belt", "Town Hall"], region: "Northern TN" },
  { name: "Tiruvannamalai", lat: 12.2253, lng: 79.0747, zone: "Tiruvannamalai Municipality", wards: ["Girivalam Path", "Arunachaleswarar Temple", "Polur Rd", "Chengam Rd"], region: "Northern TN" },
  { name: "Viluppuram", lat: 11.9401, lng: 79.4861, zone: "Viluppuram Municipality", wards: ["Old Bus Stand", "Tindivanam", "Gingee Fort Zone", "East Coast Link"], region: "Northern TN" },
  { name: "Cuddalore", lat: 11.7480, lng: 79.7714, zone: "Cuddalore Municipality", wards: ["Silver Beach Zone", "Manjakuppam", "OT Cuddalore", "Port Area"], region: "Northern TN" },
  { name: "Kallakurichi", lat: 11.7384, lng: 78.9639, zone: "Kallakurichi Municipality", wards: ["Salem Main Rd", "Sankarapuram", "Chinnasalem", "Ulundurpet"], region: "Northern TN" },

  // Western / Kongu Region
  { name: "Coimbatore", lat: 11.0168, lng: 76.9558, zone: "Coimbatore City Corp", wards: ["Gandhipuram", "RS Puram", "Peelamedu", "Ukkadam", "Saravanampatti", "Singanallur"], region: "Western / Kongu" },
  { name: "Tiruppur", lat: 11.1085, lng: 77.3411, zone: "Tiruppur Corp", wards: ["Avinashi Road", "Old Bus Stand", "Palladam Road", "Velliyankadu"], region: "Western / Kongu" },
  { name: "Erode", lat: 11.3410, lng: 77.7172, zone: "Erode Corp", wards: ["Perundurai Road", "Brough Road", "Surampatti", "Veerappanchatram"], region: "Western / Kongu" },
  { name: "Salem", lat: 11.6643, lng: 78.1460, zone: "Salem City Corp", wards: ["New Bus Stand", "Hasthampatti", "Fairlands", "Suramangalam", "Ammapet"], region: "Western / Kongu" },
  { name: "Namakkal", lat: 11.2189, lng: 78.1674, zone: "Namakkal Municipality", wards: ["Anjaneyar Temple Rd", "Mohanur Rd", "Tiruchengode Rd", "Salem Bypass"], region: "Western / Kongu" },
  { name: "Karur", lat: 10.9601, lng: 78.0766, zone: "Karur Corp", wards: ["Textile Zone", "Thanthonimalai", "Jawahar Bazaar", "Gandhigramam"], region: "Western / Kongu" },
  { name: "Dharmapuri", lat: 12.1211, lng: 78.1582, zone: "Dharmapuri Municipality", wards: ["Collectorate Zone", "Pennagaram Rd", "Railway Station Road", "Four Roads"], region: "Western / Kongu" },
  { name: "Krishnagiri", lat: 12.5186, lng: 78.2137, zone: "Krishnagiri Municipality", wards: ["Hosur Industrial Belt", "Rayakottai Rd", "Old Pet", "Londonpet"], region: "Western / Kongu" },
  { name: "Nilgiris", lat: 11.4102, lng: 76.6950, zone: "Udhagamandalam (Ooty)", wards: ["Charring Cross", "Coonoor", "Kotagiri", "Botanical Garden Zone"], region: "Western / Kongu" },

  // Central & Cauvery Delta Region
  { name: "Tiruchirappalli", lat: 10.7905, lng: 78.7047, zone: "Tiruchirappalli Corp", wards: ["Thillai Nagar", "Srirangam", "Central Bus Stand", "Cantonment", "K.K. Nagar"], region: "Central & Delta" },
  { name: "Thanjavur", lat: 10.7870, lng: 79.1378, zone: "Thanjavur Corp", wards: ["Big Temple Road", "Medical College Rd", "Old Bus Stand", "New Housing Unit"], region: "Central & Delta" },
  { name: "Tiruvarur", lat: 10.7725, lng: 79.6365, zone: "Tiruvarur Municipality", wards: ["Thyagaraja Temple Zone", "Kudavasal", "Mannargudi", "South Street"], region: "Central & Delta" },
  { name: "Nagapattinam", lat: 10.7672, lng: 79.8449, zone: "Nagapattinam Municipality", wards: ["Velankanni Coast", "Port Area", "Public Office Rd", "Nagore"], region: "Central & Delta" },
  { name: "Mayiladuthurai", lat: 11.1075, lng: 79.6524, zone: "Mayiladuthurai Municipality", wards: ["Mayuranathar Zone", "Kaveri River Bank", "Poompuhar", "Sirkazhi"], region: "Central & Delta" },
  { name: "Pudukkottai", lat: 10.3797, lng: 78.8208, zone: "Pudukkottai Corp", wards: ["Palace Road", "Santhaiyapettai", "Machuvadi", "Alangudi"], region: "Central & Delta" },
  { name: "Ariyalur", lat: 11.1401, lng: 79.0786, zone: "Ariyalur Municipality", wards: ["Cement City Zone", "Jayankondam", "Sendurai", "Market Street"], region: "Central & Delta" },
  { name: "Perambalur", lat: 11.2342, lng: 78.8820, zone: "Perambalur Municipality", wards: ["Bypass Road", "Veppanthattai", "Alathur", "Elambalur"], region: "Central & Delta" },

  // Southern Region
  { name: "Madurai", lat: 9.9252, lng: 78.1198, zone: "Madurai City Corp", wards: ["Meenakshi Amman Temple Zone", "Mattuthavani", "Anna Nagar", "Simmakkal", "Goripalayam"], region: "Southern TN" },
  { name: "Dindigul", lat: 10.3673, lng: 77.9803, zone: "Dindigul Corp", wards: ["Rock Fort Zone", "Round Road", "Palani Road", "Nagal Nagar"], region: "Southern TN" },
  { name: "Theni", lat: 10.0104, lng: 77.4768, zone: "Theni Allinagaram", wards: ["Bodinayakanur Rd", "Periyakulam", "Cumbum", "Subban Street"], region: "Southern TN" },
  { name: "Virudhunagar", lat: 9.5680, lng: 77.9624, zone: "Virudhunagar Municipality", wards: ["Sivakasi Fireworks Belt", "Aruppukkottai", "Srivilliputhur", "Madurai Rd"], region: "Southern TN" },
  { name: "Sivaganga", lat: 9.8433, lng: 78.4809, zone: "Sivaganga Municipality", wards: ["Karaikudi Chettinad Belt", "Devakottai", "Manamadurai", "Palace Zone"], region: "Southern TN" },
  { name: "Ramanathapuram", lat: 9.3639, lng: 78.8395, zone: "Ramanathapuram Municipality", wards: ["Rameswaram Island Rd", "Mandapam Coast", "Paramakudi", "Kilakarai"], region: "Southern TN" },
  { name: "Thoothukudi", lat: 8.7642, lng: 78.1348, zone: "Thoothukudi City Corp", wards: ["Pearl City Port Zone", "Palayamkottai Rd", "Cruz Fernandez Ward", "Millerpuram"], region: "Southern TN" },
  { name: "Tirunelveli", lat: 8.7139, lng: 77.7567, zone: "Tirunelveli City Corp", wards: ["Nellaiappar Temple Zone", "Palayamkottai", "Vannarpettai", "Town Hall"], region: "Southern TN" },
  { name: "Tenkasi", lat: 8.9594, lng: 77.3150, zone: "Tenkasi Municipality", wards: ["Courtallam Falls Ward", "Kasi Viswanathar Temple", "Sankarankovil", "Kadayanallur"], region: "Southern TN" },
  { name: "Kanniyakumari", lat: 8.0883, lng: 77.5385, zone: "Nagercoil City Corp", wards: ["Cape Comorin Point", "Nagercoil Town", "Vadasery", "Colachel Coast"], region: "Southern TN" }
];

function findNearestTNDistrict(lat, lng) {
  let closest = ALL_38_DISTRICTS[0];
  let minDiff = Infinity;
  for (const d of ALL_38_DISTRICTS) {
    const dLat = d.lat - lat;
    const dLng = d.lng - lng;
    const distSq = (dLat * dLat) + (dLng * dLng);
    if (distSq < minDiff) {
      minDiff = distSq;
      closest = d;
    }
  }
  return closest;
}
