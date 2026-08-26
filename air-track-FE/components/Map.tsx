import { MapContainer, TileLayer } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import { useLocations } from "../hooks/useLocations";
import { AqiMarker } from "./AqiMarker";

export function Map() {
  const { locations, loading, error } = useLocations();

  if (loading) return <p>Ładowanie mapy...</p>;
  if (error) return <p>Błąd: {error}</p>;

  return (
    <MapContainer
      center={[52.0, 19.0]}
      zoom={6}
      style={{ height: '600px', width: '100%' }}
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      />
      {locations.map((location) => (
        <AqiMarker key={location.id} location={location} />
      ))}
    </MapContainer>
  );
}