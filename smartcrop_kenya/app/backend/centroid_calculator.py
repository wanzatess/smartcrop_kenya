import json
import re
import shapely.geometry

def clean_name(text):
    """Adds spaces before capital letters: 'AlegoUsonga' -> 'Alego Usonga'"""
    if not text: return "Unknown"
    # Logic: Find lowercase followed by uppercase and insert space
    return re.sub(r"(\w)([A-Z])", r"\1 \2", text)

def generate_location_dictionary(input_file, output_file):
    with open(input_file, 'r') as f:
        data = json.load(f)

    location_map = {}

    for feature in data['features']:
        properties = feature['properties']
        
        # 1. Extract Names
        county = properties.get('NAME_1', 'Unknown')
        raw_subcounty = properties.get('NAME_2', 'Unknown')
        display_name = clean_name(raw_subcounty)

        # 2. Calculate Centroid using Shapely
        # This handles both Polygon and MultiPolygon types automatically
        geom = shapely.geometry.shape(feature['geometry'])
        centroid = geom.centroid

        # 3. Create Entry
        # Note: GeoJSON is [Lon, Lat], we store as [Lat, Lon] for the Weather API
        location_map[display_name] = {
            "lat": round(centroid.y, 4),
            "lon": round(centroid.x, 4),
            "county": county
        }

    # 4. Save as a lightweight JSON for Android assets
    with open(output_file, 'w') as out_f:
        json.dump(location_map, out_f, indent=2)

    print(f"Success! Processed {len(location_map)} subcounties.")
    print(f"File saved to: {output_file}")

# Execution
generate_location_dictionary('gadm41_KEN_2.json', 'locations_dict.json')