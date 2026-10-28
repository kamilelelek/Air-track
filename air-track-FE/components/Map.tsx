import { useEffect } from "react";
import { MapContainer, TileLayer, useMap } from "react-leaflet";
import type { LatLngBoundsExpression } from "leaflet";
import "leaflet/dist/leaflet.css";
import type { Location } from "../types/Location";
import { AqiMarker } from "./AqiMarker";

const POLAND_BOUNDS: LatLngBoundsExpression = [[49.0, 14.1], [54.9, 24.2]];

interface MapProps {
  locations: Location[];
  selectedId: number | null;
  onSelect: (id: number) => void;
}

// Brings a location picked from the sidebar list into view; markers clicked on the map are already visible.
function PanToSelected({ location }: { location: Location | undefined }) {
  const map = useMap();

  useEffect(() => {
    if (!location) return;
    const position: [number, number] = [location.latitude, location.longitude];
    if (!map.getBounds().pad(-0.1).contains(position)) {
      map.flyTo(position, Math.max(map.getZoom(), 8), { duration: 0.6 });
    }
  }, [map, location]);

  return null;
}

export function Map({ locations, selectedId, onSelect }: MapProps) {
  return (
    <MapContainer
      bounds={POLAND_BOUNDS}
      boundsOptions={{ padding: [24, 24] }}
      zoomControl={false}
      attributionControl
      className="map"
    >
      <TileLayer
        url={`https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png?key=${import.meta.env.VITE_CARTO_API_KEY}`}
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
        subdomains="abcd"
        maxZoom={20}
      />
      {locations.map((location) => (
        <AqiMarker
          key={location.id}
          location={location}
          selected={location.id === selectedId}
          onSelect={onSelect}
        />
      ))}
      <PanToSelected location={locations.find((l) => l.id === selectedId)} />
    </MapContainer>
  );
}
