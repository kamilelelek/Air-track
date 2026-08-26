import { useState, useEffect } from 'react';
import type { Location } from "../types/Location";
import { getAllLocation } from '../api/Locations';


interface UseLocationResult{
  locations: Location[];
  loading: boolean;
  error: string | null;
}

export function useLocations(): UseLocationResult{
  const [locations, setLocations] = useState<Location[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() =>{
    async function fetchData() {
      try{
          setLoading(true);
          const data= await getAllLocation();
          setLocations(data);
          setError(null);
      }catch(error){
        setError('Failed to get locations');
      }finally{
        setLoading(false);
      }
    }
    fetchData();
  }, []);
  return {locations, loading, error}
}