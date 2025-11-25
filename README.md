# **HENloader LP – SOURCE**

## **Overview**

**HENloader LP** is a PlayStation 4 Blu-ray–based exploit loader that combines **Lapse** and **Poopsploit** exploits with **GoldHEN** support.
This tool enables homebrew loading on PS4 firmware versions **9.00 up to 12.52**, depending on the exploit used.

It is designed to run from a **Blu-ray disc ISO**, allowing a stable, offline, self-contained jailbreak method without using a browser.

---

## **Features**

* **Dual Exploit Support**

  * **Lapse exploit:** PS4 firmware **9.00 – 12.02**
  * **Poopsploit:** PS4 firmware **9.00 – 12.52**

* **Integrated Homebrew Enabler**

  * Comes with **GoldHEN v2.4b18.7** included in the release.

* **Blu-ray ISO Loader**

  * The exploit runs directly from a burned BD-R / BD-RE disc.

* **Payload Auto-Loader**

  * Reads `payload.bin` from USB and copies it to `/data/payload.bin`.

* **Network Loader (Advanced)**

  * Lapse JAR loader on port **9020**
  * Remote JAR loader on port **9025**

* **AIO Fix Support**

  * Additional patches for stability, black-screen fixes, and save-data safety.

---

## **Firmware Compatibility**

| Firmware Version  | Supported Exploit |
| ----------------- | ----------------- |
| **9.00 – 12.02**  | Lapse + Poops     |
| **12.02 – 12.52** | Poops only        |

---

## **Repository Structure**

```
HENloader_Source/
├── api/
├── external/
├── sandbox/
├── InitXlet.java
├── MessagesOutputStream.java
├── Screen.java
├── bluray.InitXlet.perm
└── README.md  (this file)
```

---

## **Requirements**

* PS4 (firmware 9.00–12.52)
* Blu-ray burner
* **BD-R or BD-RE** disc
* USB drive (FAT32 or exFAT)
* `payload.bin` (GoldHEN or custom payload)

---

## **Installation & Usage**

### **1. Download**

Grab the latest release of **HENloader LP** from the Releases section.

### **2. Prepare USB**

1. Format USB to **FAT32** or **exFAT**.
2. Place your payload file in the root and rename it to:

```
payload.bin
```

### **3. Burn the ISO**

* Burn the ISO from the release package onto a BD-R or BD-RE.
* Burn at **low speed** for best stability.

### **4. Prepare PS4**

* Enable **HDCP** (required for Blu-ray playback).
* Recommended: disable auto-updates.

### **5. Execute the Exploit**

1. Insert the **USB** into the PS4.
2. Insert the **Blu-ray disc**.
3. Choose **Lapse** or **Poops** mode depending on your firmware.
4. Wait for the exploit to run and load GoldHEN.
5. Disc may eject automatically — this is normal.

### **6. After Jailbreak**

* Access GoldHEN features:

  * PKG installation
  * FTP server
  * Debug settings
  * Homebrew loader

---

## **Troubleshooting**

* **Exploit fails / freezes**

  * Reboot the PS4
  * Reinsert disc
  * Try again (timing is normal with BD-JB based exploits)

* **Payload not loaded**

  * Ensure USB is connected **before** inserting disc
  * Check `payload.bin` filename
  * Verify USB format (FAT32/exFAT)

* **Black screen**

  * Apply AIO fix if using custom loaders
  * Use a BD-RE and reburn the ISO

---

## **Warnings**

* Jailbreaking voids device warranty.
* Use only trusted payloads.
* Risk of system instability or data loss.
* Do **not** update firmware beyond supported versions if you want to keep exploit capability.

---

## **Credits**

* **GoldHEN Team** – payload + integration
* **Lapse / Poopsploit Developers** – exploit research
* **BD-J / Blu-ray Exploit Community**
* All contributors who improved loader stability and compatibility

