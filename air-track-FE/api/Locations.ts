import axios from "axios";
import { Location } from "../types/Location";
import { Measurement } from "../types/Measurement";
import { PaginatedResponse } from "../types/PaginatedResponse.ts";


const BASE_URL= 'https://localhost:8085/api/v1'

export async function getAllLocation(): Promise<Location[]> {
  const response = await axios.get<Location[]>(`${BASE_URL}/locations`);
  return response.data;
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