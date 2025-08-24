export const FireCauses = [
  { cause: "Miscellaneous", recommendations: ["Follow basic fire safety rules."] },
  { cause: "Lightning", recommendations: ["Stay indoors during storms."] },
  { cause: "Debris Burning", recommendations: ["Burn waste only in safe, legal places."] },
  { cause: "Campfire", recommendations: ["Use official campfire spots only."] },
  { cause: "Equipment Use", recommendations: ["Check equipment for sparks before use."] },
  { cause: "Arson", recommendations: ["Report suspicious fire activity quickly."] },
  { cause: "Children", recommendations: ["Teach kids about fire dangers early."] },
  { cause: "Railroad", recommendations: ["Inspect rails for fire hazards regularly."] },
  { cause: "Smoking", recommendations: ["Never litter cigarettes in nature."] },
  { cause: "Powerline", recommendations: ["Maintain power lines to prevent sparks."] },
  { cause: "Structure", recommendations: ["Keep buildings clear of flammable debris."] },
  { cause: "Fireworks", recommendations: ["Use fireworks only in safe areas."] },
  { cause: "Missing/Undefined", recommendations: ["Be extra careful; cause is unknown."] },
];

// Mapping from STAT_CAUSE_DESCR to encoded value
export const STAT_CAUSE_DESCR_TO_ENCODED = {
  "Arson": 0,
  "Campfire": 1,
  "Children": 2,
  "Debris Burning": 3,
  "Equipment Use": 4,
  "Fireworks": 5,
  "Lightning": 6,
  "Miscellaneous": 7,
  "Missing/Undefined": 8,
  "Powerline": 9,
  "Railroad": 10,
  "Smoking": 11,
  "Structure": 12,
};
