import type { AqiCategory } from "./AqiCategory";
import type { Measurement } from "./Measurement";

export interface Location{
  id: number;
  name: string;
  city: string;
  latitude: number;
  longitude: number;
  measurements: Measurement[];
  aqiCategory: AqiCategory;
}