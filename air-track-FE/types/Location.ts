import type { AqiCategory } from "./AqiCategory";
import type { Measurement } from "./Measurement";

export interface Location{
  id: number;
  name: string;
  city: string;
  country: string;
  latitude: number;
  longitude: number;
  measurements: Measurement[];
  pm25: number | null;
  // null when the station has no recent PM2.5 reading to classify
  aqiCategory: AqiCategory | null;
}
