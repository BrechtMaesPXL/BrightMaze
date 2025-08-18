<template>

    <div ref="container" class="viewer"></div>
    
  
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';


const container = ref(null);
let renderer, camera, scene, controls, animationId, mixer;
const clock = new THREE.Clock();

let currentAction = null;
const actions = {};
const visemeMeshes = [];

const visemes = [
  'viseme_sil', 'viseme_PP', 'viseme_FF', 'viseme_TH',
  'viseme_DD', 'viseme_kk', 'viseme_CH', 'viseme_SS',
  'viseme_nn', 'viseme_RR', 'viseme_aa', 'viseme_E',
  'viseme_I', 'viseme_O', 'viseme_U'
];
const text = ref('');

const letterToViseme = {
  a: 'viseme_aa', b: 'viseme_PP', c: 'viseme_CH', d: 'viseme_DD',
  e: 'viseme_E', f: 'viseme_FF', g: 'viseme_kk', h: 'viseme_SS',
  i: 'viseme_I', j: 'viseme_CH', k: 'viseme_kk', l: 'viseme_nn',
  m: 'viseme_PP', n: 'viseme_nn', o: 'viseme_O', p: 'viseme_PP',
  q: 'viseme_kk', r: 'viseme_RR', s: 'viseme_SS', t: 'viseme_DD',
  u: 'viseme_U', v: 'viseme_FF', w: 'viseme_U', x: 'viseme_SS',
  y: 'viseme_I', z: 'viseme_SS'
};

async function playTextAsVisemes() {
  const letters = text.value.toLowerCase().replace(/[^a-z]/g, '').split('');

  for (let i = 0; i < letters.length; i++) {
    const letter = letters[i];
    const viseme = letterToViseme[letter] || 'viseme_sil';

    applyViseme(viseme);
    await new Promise(resolve => setTimeout(resolve, 75)); // tijd tussen klanken
  }

  applyViseme('viseme_sil'); // mond sluiten op einde
}

function applyViseme(name) {
  visemeMeshes.forEach((mesh) => {
    const dict = mesh.morphTargetDictionary;
    const influences = mesh.morphTargetInfluences;

    // Reset alle visemes
    visemes.forEach(v => {
      const idx = dict?.[v];
      if (idx !== undefined) influences[idx] = 0;
    });

    // Zet gewenste viseme aan
    const index = dict?.[name];
    if (index !== undefined) influences[index] = 1;
  });
}
function speakTextAndLipsync() {
  const utterance = new SpeechSynthesisUtterance(text.value);
  utterance.lang = 'nl-NL'; // Nederlandse stem
   utterance.rate = 0.8;
  utterance.onstart = () => {
    playTextAsVisemes(); // lipsync begint pas als spraak écht start
  };

  speechSynthesis.speak(utterance);
}

function loadAndPlayAnimation(path, name) {
  const fbxLoader = new FBXLoader();
  fbxLoader.load(path, (anim) => {
    const clip = anim.animations[0];
    const action = mixer.clipAction(clip);
    actions[name] = action;
    if (name === 'Idle') {
      action.play();
      currentAction = action;
    }
  }, undefined, (err) => {
    console.error(`Fout bij laden van ${name}:`, err);
  });
}

function playAnimation(name) {
  if (currentAction) currentAction.stop();
  if (actions[name]) {
    actions[name].reset().play();
    currentAction = actions[name];
  }
}

const playDance = () => playAnimation("Dancing");
const playWalk = () => playAnimation("Walking");
const playRun = () => playAnimation("Running");
const playAngry = () => playAnimation("Angry");
const playGreeting = () => playAnimation("Greeting");
const playIdle = () => playAnimation("Idle");

onMounted(() => {
  scene = new THREE.Scene();
  //scene.background = new THREE.Color('#87CEEB');

  camera = new THREE.PerspectiveCamera(
    35,
    container.value.clientWidth / container.value.clientHeight,
    0.1,
    1000
  );

  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(container.value.clientWidth, container.value.clientHeight);
  container.value.appendChild(renderer.domElement);

  // Licht
  const hemiLight = new THREE.HemisphereLight(0xffffff, 0x444444, 6);
  hemiLight.position.set(0, 20, 0);
  scene.add(hemiLight);

  const dirLight = new THREE.DirectionalLight(0xffffff, 1.5
  );
  dirLight.position.set(3, 10, 10);
  scene.add(dirLight);

  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;

  const loader = new GLTFLoader();
  loader.load('/models/model.glb', (gltf) => {
    const model = gltf.scene;
    scene.add(model);

    model.traverse((child) => {
      if (child.isMesh && child.morphTargetInfluences && child.morphTargetInfluences.length > 0) {
        visemeMeshes.push(child);
        console.log('Mesh met morph targets:', child.name);
        console.log('Beschikbare targets:', child.morphTargetDictionary);
      }
    });

    // Camera positie bepalen
    const box = new THREE.Box3().setFromObject(model);
    const center = new THREE.Vector3();
    box.getCenter(center);
    const faceFocus = new THREE.Vector3(
      center.x,
      center.y + (box.max.y - box.min.y) * 0.4,
      center.z
    );
    camera.position.set(faceFocus.x, faceFocus.y, faceFocus.z + 1.2);
    controls.target.copy(faceFocus);
    controls.update();

    mixer = new THREE.AnimationMixer(model);
    loadAndPlayAnimation('/animations/Idle.fbx', 'Idle');
    loadAndPlayAnimation('/animations/Dancing.fbx', 'Dancing');
    loadAndPlayAnimation('/animations/Walking.fbx', 'Walking');
    loadAndPlayAnimation('/animations/Run.fbx', 'Running');
    loadAndPlayAnimation('/animations/Angry.fbx', 'Angry');
    loadAndPlayAnimation('/animations/Greeting.fbx', 'Greeting');

  }, undefined, (error) => {
    console.error('Fout bij laden van GLB model:', error);
  });

  const animate = () => {
    animationId = requestAnimationFrame(animate);
    const delta = clock.getDelta();
    if (mixer) mixer.update(delta);
    controls.update();
    renderer.render(scene, camera);
  };
  animate();
});

onBeforeUnmount(() => {
  cancelAnimationFrame(animationId);
  if (controls) controls.dispose();
  if (renderer) renderer.dispose();
});

const speak = (textje) => {
  console.log('speak 19', textje);
  if (!textje) return;
  text.value = textje; // Update de reactive text ref
  playIdle();
  speakTextAndLipsync();
   // Roep de functie aan
};
function greeting() {
  console.log('greeting 21');
  playGreeting();
}

defineExpose({ speak, greeting });
</script>

<style scoped>
.viewer {
  width: 100%;
  height: 100%;
}




</style>
