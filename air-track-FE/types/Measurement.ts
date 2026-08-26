import { AqiCategory } from "./AqiCategory";

export interface Measurement {
  id: number;
  location: Location;
  parameter: string; 
  value: number;
  unit: string;
  measuredAt: string;
  fetchedAt: string; 
  aqiCategory: AqiCategory;
}