export async function fetchData(url, options){
  try{
      const response = await fetch(url, options);
      if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || errorData.title || errorData || 'Network response was not OK');
      }
      const text = await response.text();
      if (!text) {
        return {};
      }
      
      const trimmedText = text.trim();
      if (trimmedText.startsWith('{') || trimmedText.startsWith('[')) {
        return JSON.parse(trimmedText);
      } else {
        return text;
      }
  }catch (error){
      console.log(error)
      throw error;
  }
}
