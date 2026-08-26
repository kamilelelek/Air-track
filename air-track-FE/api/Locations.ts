import axios from "axios";
import type { Location } from "../types/Location";
import type { Measurement } from "../types/Measurement";
import type { PaginatedResponse } from "../types/PaginatedResponse.ts";


const BASE_URL = '/api/v1';

interface LocationApiResponse {
  station: {
    id: number;
    name: string;
    city: string;
    latitude: number;
    longitude: number;
  };
  latestMeasurements: Measurement[];
  aqi: Location['aqiCategory'] | null;
}

export async function getAllLocation(): Promise<Location[]> {
  const response = await axios.get<LocationApiResponse[]>(`${BASE_URL}/locations`);
  return response.data.map(({ station, latestMeasurements, aqi }) => ({
    ...station,
    measurements: latestMeasurements,
    aqiCategory: aqi ?? 'ZLY',
  }));
}

export async function getAllDetailsOfLocation(id: number): Promise<Location>{
  const response = await axios.get<Location>(`${BASE_URL}/locations/${id}`);
  return response.data;
}
export async function getAllMeasurementsOfLocation(id:number,
  page:number
): Promise<PaginatedResponse<Measurement>> {
 const response= await axios.get<PaginatedResponse<Measurement>>(
  `${BASE_URL}/locations/${id}/measurements`,
  {params: {page}}
 );
 return response.data;
}

export async function getLatestMeasurements(): Promise<Measurement[]> {
  const response = await axios.get<Measurement[]>(`${BASE_URL}/measurements/latest`);
  return response.data;
}