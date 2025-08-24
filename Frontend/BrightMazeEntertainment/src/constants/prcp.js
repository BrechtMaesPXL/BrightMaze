// Converter for prcp category string to numeric range
// Example: 'no rain' -> 0, 'low' -> 1, 'medium' -> 2, 'high' -> 3

export function prcpCategoryToNumber(category) {
  switch (category) {
    case 'no rain':
      return 0
    case 'low':
      return 1
    case 'medium':
      return 2
    case 'high':
      return 3
    default:
      return null
  }
}

export function numberToPrcpCategory(value) {
  if (value === 0) return 'no rain'
  if (value > 0 && value <= 2) return 'low'
  if (value > 2 && value <= 10) return 'medium'
  if (value > 10) return 'high'
  return null
}
