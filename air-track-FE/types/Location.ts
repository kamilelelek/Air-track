import { Measurement } from "./Measurement";

export interface Location{
  id: number;
  name: string;
  city: string;
  latitude: number;
  longtitude: number;
  measurements: Measurement[];
}