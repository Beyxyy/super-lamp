// src/app/components/book/book.component.ts
import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ElementRef,
  ViewChild,
  Input,
  HostListener,
} from '@angular/core';
import * as THREE from 'three';
import { PageService } from '../services/page.service';
import { easing } from 'maath';
import { Subscription } from 'rxjs';

const PAGE_WIDTH = 1.28;
const PAGE_HEIGHT = 1.71;
const PAGE_DEPTH = 0.003;
const PAGE_SEGMENTS = 30;
const SEGMENT_WIDTH = PAGE_WIDTH / PAGE_SEGMENTS;

const easingFactor = 1.5;
const easingFactorFold = 0.1;
const insideCurveStrength = 0.18;
const outsideCurveStrength = 0.05;
const turningCurveStrength = 0.09;


const whiteColor = new THREE.Color('white');
const emissiveColor = new THREE.Color('orange');

const pageMaterials = [
  new THREE.MeshStandardMaterial({ color: whiteColor }),
  new THREE.MeshStandardMaterial({ color: '#111' }),
  new THREE.MeshStandardMaterial({ color: whiteColor }),
  new THREE.MeshStandardMaterial({ color: whiteColor }),
];

const pages: { front: string; back: string }[] = [
  { front: 'page1-front', back: 'page1-back' },
  { front: 'page2-front', back: 'page2-back' },
  { front: 'page2-front', back: 'page2-back' },
  { front: 'page2-front', back: 'page2-back' },
  { front: 'page2-front', back: 'page2-back' },
  { front: 'page2-front', back: 'page2-back' },
  { front: 'page2-front', back: 'page2-back' },
  // Ajoute tes pages ici
];

@Component({
  selector: 'app-book',
  standalone: true,
  template: `<div #container></div>`,
  styles: [`:host { display: block; }`],
})
export class BookComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('container') container!: ElementRef<HTMLDivElement>;

  private scene!: THREE.Scene;
  private camera!: THREE.PerspectiveCamera;
  private renderer!: THREE.WebGLRenderer;
  private group!: THREE.Group;
  private skinnedMeshes: THREE.SkinnedMesh[] = [];
  private bonesArray: THREE.Bone[][] = [];
  private pageSubscription!: Subscription;
  private delayedPage: number = 0;
  private page: number = 0;
  private turningAt: number = 0;
  private lastOpened: boolean[] = [];
  private highlighted: boolean[] = [];
  private textures: { [key: string]: THREE.Texture } = {};
  private textureLoader = new THREE.TextureLoader();
  private roughnessTexture!: THREE.Texture;

  constructor(private pageService: PageService) {}

  ngOnInit() {
    this.initScene();
    this.initCamera();

    this.pageSubscription = this.pageService.page$.subscribe((page : number) => {
      this.page = page;
      this.updateDelayedPage();
    });
  }

  ngAfterViewInit() {
    this.initRenderer();
    // Démarrer l'animation après que le renderer soit prêt
    setTimeout(() => this.animate(), 150);
    this.initAsync(); // Charger en parallèle
  }

  private async initAsync() {
    await this.loadTextures();
    this.initBook();
  }

  ngOnDestroy() {
    this.pageSubscription.unsubscribe();
    window.removeEventListener('resize', this.onResize);
    this.renderer.dispose();
  }

  private initScene() {
    this.scene = new THREE.Scene();
    this.scene.background = new THREE.Color(0xf0f0f0);
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
    this.scene.add(ambientLight);
    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
    directionalLight.position.set(1, 1, 1);
    this.scene.add(directionalLight);
  }

  private initCamera() {
    this.camera = new THREE.PerspectiveCamera(
      75,
      window.innerWidth / window.innerHeight,
      0.1,
      1000
    );
    this.camera.position.set(0, 0, 5);
    this.camera.lookAt(0, 0, 0);
  }

  private initRenderer() {
    this.renderer = new THREE.WebGLRenderer({ antialias: true });
    this.renderer.setPixelRatio(window.devicePixelRatio);
    this.renderer.setClearColor(0xf0f0f0);
    
    const container = this.container.nativeElement;
    container.style.cssText = 'position:fixed;top:0;left:0;width:100vw;height:100vh;margin:0;padding:0;z-index:10000;display:block';
    
    container.appendChild(this.renderer.domElement);
    
    this.onResize();
    
    window.addEventListener('resize', this.onResize.bind(this));
  }

  private onResize() {
    const width = window.innerWidth;
    const height = window.innerHeight;
    if (width > 0 && height > 0) {
      this.camera.aspect = width / height;
      this.camera.updateProjectionMatrix();
      this.renderer.setSize(width, height);
    }
  }

  private async loadTextures() {
    const texturePromises: Promise<void>[] = [];
    pages.forEach((page) => {
      texturePromises.push(
        new Promise((resolve) => {
          this.textureLoader.load(
            `/assets/textures/${page.front}.jpg`,
            (texture) => {
              texture.colorSpace = THREE.SRGBColorSpace;
              this.textures[`${page.front}`] = texture;
              resolve();
            },
            () => {
              console.warn(`Texture not found: /assets/textures/${page.front}.jpg`);
              resolve();
            }
          );
        })
      );
      texturePromises.push(
        new Promise((resolve) => {
          this.textureLoader.load(
            `/assets/textures/${page.back}.jpg`,
            (texture) => {
              texture.colorSpace = THREE.SRGBColorSpace;
              this.textures[`${page.back}`] = texture;
              resolve();
            },
            () => {
              console.warn(`Texture not found: /assets/textures/${page.back}.jpg`);
              resolve();
            }
          );
        })
      );
    });
    texturePromises.push(
      new Promise((resolve) => {
        this.textureLoader.load(
          `/assets/textures/book-cover-roughness.jpg`,
          (texture) => {
            this.roughnessTexture = texture;
            resolve();
          },
          () => {
            console.warn('Texture not found: /assets/textures/book-cover-roughness.jpg');
            resolve();
          }
        );
      })
    );
    
    // Timeout de sécurité (5 secondes)
    const timeoutPromise = new Promise<void>((resolve) => {
      setTimeout(() => {
        console.warn('Texture loading timed out, using fallback');
        resolve();
      }, 5000);
    });
    
    await Promise.race([
      Promise.all(texturePromises),
      timeoutPromise
    ]);
  }

  private initBook() {
    this.group = new THREE.Group();
    this.group.rotation.y = -Math.PI / 2;
    this.scene.add(this.group);

    pages.forEach((pageData, index) => {
      this.createPage(index, pageData.front, pageData.back);
    });
  }

  private createPage(number: number, front: string, back: string) {
    const pageGeometry = new THREE.BoxGeometry(
      PAGE_WIDTH,
      PAGE_HEIGHT,
      PAGE_DEPTH,
      PAGE_SEGMENTS,
      2
    );
    pageGeometry.translate(PAGE_WIDTH / 2, 0, 0);

    const position = pageGeometry.attributes['position'];
    const vertex = new THREE.Vector3();
    const skinIndexes: number[] = [];
    const skinWeights: number[] = [];

    for (let i = 0; i < position.count; i++) {
      vertex.fromBufferAttribute(position, i);
      const x = vertex.x;
      const skinIndex = Math.max(0, Math.floor(x / SEGMENT_WIDTH));
      const skinWeight = (x % SEGMENT_WIDTH) / SEGMENT_WIDTH;
      skinIndexes.push(skinIndex, skinIndex + 1, 0, 0);
      skinWeights.push(1 - skinWeight, skinWeight, 0, 0);
    }

    pageGeometry.setAttribute(
      'skinIndex',
      new THREE.Uint16BufferAttribute(skinIndexes, 4)
    );
    pageGeometry.setAttribute(
      'skinWeight',
      new THREE.Float32BufferAttribute(skinWeights, 4)
    );

    const bones: THREE.Bone[] = [];
    for (let i = 0; i <= PAGE_SEGMENTS; i++) {
      const bone = new THREE.Bone();
      bones.push(bone);
      if (i === 0) {
        bone.position.x = 0;
      } else {
        bone.position.x = SEGMENT_WIDTH;
      }
      if (i > 0) {
        bones[i - 1].add(bone);
      }
    }
    const skeleton = new THREE.Skeleton(bones);
    this.bonesArray.push(bones);

    const materials = [...pageMaterials];
    materials.push(
      new THREE.MeshStandardMaterial({
        color: whiteColor,
        map: this.textures[front],
        roughnessMap: number === 0 ? this.roughnessTexture : undefined,
        roughness: number === 0 ? 1 : 0.1,
        emissive: emissiveColor,
        emissiveIntensity: 0,
      })
    );
    materials.push(
      new THREE.MeshStandardMaterial({
        color: whiteColor,
        map: this.textures[back],
        roughnessMap: number === pages.length - 1 ? this.roughnessTexture : undefined,
        roughness: number === pages.length - 1 ? 1 : 0.1,
        emissive: emissiveColor,
        emissiveIntensity: 0,
      })
    );

    const mesh = new THREE.SkinnedMesh(pageGeometry, materials);
    mesh.castShadow = true;
    mesh.receiveShadow = true;
    mesh.frustumCulled = false;
    mesh.add(skeleton.bones[0]);
    mesh.bind(skeleton);
    mesh.position.z = -number * PAGE_DEPTH;
    this.skinnedMeshes.push(mesh);
    this.group.add(mesh);

    this.lastOpened.push(false);
    this.highlighted.push(false);
  }

  private updateDelayedPage() {
    if (this.delayedPage === this.page) return;
    const direction = this.page > this.delayedPage ? 1 : -1;
    const timeout = Math.abs(this.page - this.delayedPage) > 2 ? 100 : 300;
    setTimeout(() => {
      this.delayedPage += direction;
      this.updateDelayedPage();
    }, timeout);
  }

  private animate() {
    requestAnimationFrame(() => this.animate());
    const delta = 0.016; // Approximation de 60 FPS

    this.skinnedMeshes.forEach((skinnedMesh, index) => {
      const bones = this.bonesArray[index];
      const opened = this.delayedPage > index;
      const bookClosed = this.delayedPage === 0 || this.delayedPage === pages.length;

      if (this.lastOpened[index] !== opened) {
        this.turningAt = Date.now();
        this.lastOpened[index] = opened;
      }

      const turningTime = Math.min(400, Date.now() - this.turningAt) / 400;
      const turningTimeSin = Math.sin(turningTime * Math.PI);

      const targetRotation = opened ? -Math.PI / 2 : Math.PI / 2;
      const bookRotation = bookClosed ? 0 : THREE.MathUtils.degToRad(index * 0.8);

      bones.forEach((bone, i) => {
        const target = i === 0 ? this.skinnedMeshes[index] : bones[i];
        const insideCurveIntensity = i < 8 ? Math.sin(i * 0.2 + 0.25) : 0;
        const outsideCurveIntensity = i >= 8 ? Math.cos(i * 0.3 + 0.09) : 0;
        const turningIntensity = Math.sin(i * Math.PI * (1 / bones.length)) * turningTimeSin;

        const rotationAngle =
          insideCurveStrength * insideCurveIntensity * (targetRotation + bookRotation) -
          outsideCurveStrength * outsideCurveIntensity * (targetRotation + bookRotation) +
          turningCurveStrength * turningIntensity * (targetRotation + bookRotation);

        const foldRotationAngle = THREE.MathUtils.degToRad(Math.sign(targetRotation) * 2);
        if (bookClosed) {
          if (i === 0) {
            target.rotation.y = targetRotation;
          } else {
            target.rotation.y = 0;
          }
        } else {
          easing.dampAngle(target.rotation, 'y', rotationAngle, easingFactor, delta);
        }

        const foldIntensity = i > 8 ? Math.sin(i * Math.PI * (1 / bones.length) - 0.5) * turningTimeSin : 0;
        easing.dampAngle(target.rotation, 'x', foldRotationAngle * foldIntensity, easingFactorFold, delta);
      });

      const emissiveIntensity = this.highlighted[index] ? 0.22 : 0;
      const materials = skinnedMesh.material as THREE.MeshStandardMaterial[];
      materials[4].emissiveIntensity =
        materials[5].emissiveIntensity =
        THREE.MathUtils.lerp(
          materials[4].emissiveIntensity,
          emissiveIntensity,
          0.1
        );
    });

    this.renderer.render(this.scene, this.camera);
  }

  @HostListener('mousemove', ['$event'])
  onMouseMove(event: MouseEvent) {
    const rect = this.container.nativeElement.getBoundingClientRect();
    const mouse = new THREE.Vector2(
      ((event.clientX - rect.left) / rect.width) * 2 - 1,
      -((event.clientY - rect.top) / rect.height) * 2 + 1
    );
    const raycaster = new THREE.Raycaster();
    raycaster.setFromCamera(mouse, this.camera);
    const intersects = raycaster.intersectObjects(this.skinnedMeshes);

    this.highlighted.fill(false);
    if (intersects.length > 0) {
      const index = this.skinnedMeshes.indexOf(intersects[0].object as THREE.SkinnedMesh);
      if (index !== -1) {
        this.highlighted[index] = true;
      }
    }
  }

  @HostListener('click', ['$event'])
  onClick(event: MouseEvent) {
    const rect = this.container.nativeElement.getBoundingClientRect();
    const mouse = new THREE.Vector2(
      ((event.clientX - rect.left) / rect.width) * 2 - 1,
      -((event.clientY - rect.top) / rect.height) * 2 + 1
    );
    const raycaster = new THREE.Raycaster();
    raycaster.setFromCamera(mouse, this.camera);
    const intersects = raycaster.intersectObjects(this.skinnedMeshes);

    if (intersects.length > 0) {
      const index = this.skinnedMeshes.indexOf(intersects[0].object as THREE.SkinnedMesh);
      if (index !== -1) {
        const opened = this.delayedPage > index;
        this.pageService.setPage(opened ? index : index + 1);
        this.highlighted[index] = true;
      }
    }
  }
}