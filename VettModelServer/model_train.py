import torch
import torch.nn as nn
import torch.optim as optim
from torchvision import datasets, transforms
from torch.utils.data import DataLoader
import os
import copy
import time
from torchvision.models import efficientnet_b0, EfficientNet_B0_Weights
from torch.utils.data import random_split

class FineTuning:
    def __init__(self, train_path, model_name='efficientnet', epoch=10, batch_size=32, lr=0.001, patience=3, val_split=0.2):
        self.train_path = train_path
        self.model_name = model_name
        self.epoch = epoch
        self.batch_size = batch_size
        self.lr = lr
        self.patience = patience
        self.val_split = val_split

        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model = self._get_model()
        self.criterion = nn.CrossEntropyLoss()
        self.optimizer = optim.Adam(self.model.parameters(), lr=self.lr)
        self.train_loader, self.val_loader = self._get_dataloaders()

    def _get_dataloaders(self):
        transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406],
                                 std=[0.229, 0.224, 0.225])
        ])
        dataset = datasets.ImageFolder(root=self.train_path, transform=transform)
        val_size = int(len(dataset) * self.val_split)
        train_size = len(dataset) - val_size
        train_dataset, val_dataset = random_split(dataset, [train_size, val_size])

        train_loader = DataLoader(train_dataset, batch_size=self.batch_size, shuffle=True)
        val_loader = DataLoader(val_dataset, batch_size=self.batch_size, shuffle=False)
        return train_loader, val_loader

    def _get_model(self):
        if self.model_name == 'efficientnet':
            model = efficientnet_b0(weights=EfficientNet_B0_Weights.IMAGENET1K_V1)
            num_ftrs = model.classifier[1].in_features
            model.classifier[1] = nn.Linear(num_ftrs, self._get_num_classes())
            return model.to(self.device)
        else:
            raise ValueError("지원하지 않는 모델입니다.")

    def _get_num_classes(self):
        class_names = os.listdir(self.train_path)
        return len(class_names)

    def training(self):
        since = time.time()
        best_model_wts = copy.deepcopy(self.model.state_dict())
        best_val_loss = float('inf')
        epochs_no_improve = 0

        history = {'train_loss': [], 'train_acc': [], 'val_loss': [], 'val_acc': []}

        for epoch in range(self.epoch):
            self.model.train()
            running_loss, running_corrects = 0.0, 0
            for inputs, labels in self.train_loader:
                inputs, labels = inputs.to(self.device), labels.to(self.device)
                self.optimizer.zero_grad()
                outputs = self.model(inputs)
                loss = self.criterion(outputs, labels)
                loss.backward()
                self.optimizer.step()
                _, preds = torch.max(outputs, 1)
                running_loss += loss.item() * inputs.size(0)
                running_corrects += torch.sum(preds == labels.data)

            train_loss = running_loss / len(self.train_loader.dataset)
            train_acc = running_corrects.double() / len(self.train_loader.dataset)

            self.model.eval()
            val_loss, val_corrects = 0.0, 0
            with torch.no_grad():
                for inputs, labels in self.val_loader:
                    inputs, labels = inputs.to(self.device), labels.to(self.device)
                    outputs = self.model(inputs)
                    loss = self.criterion(outputs, labels)
                    _, preds = torch.max(outputs, 1)
                    val_loss += loss.item() * inputs.size(0)
                    val_corrects += torch.sum(preds == labels.data)

            val_loss = val_loss / len(self.val_loader.dataset)
            val_acc = val_corrects.double() / len(self.val_loader.dataset)

            history['train_loss'].append(train_loss)
            history['train_acc'].append(train_acc.item())
            history['val_loss'].append(val_loss)
            history['val_acc'].append(val_acc.item())

            print(f"[{epoch+1}/{self.epoch}] "
                  f"Train Loss: {train_loss:.4f}, Acc: {train_acc:.4f} | "
                  f"Val Loss: {val_loss:.4f}, Acc: {val_acc:.4f}")

            if val_loss < best_val_loss:
                best_val_loss = val_loss
                best_model_wts = copy.deepcopy(self.model.state_dict())
                epochs_no_improve = 0
            else:
                epochs_no_improve += 1
                if epochs_no_improve >= self.patience:
                    print(f"Early stopping triggered at epoch {epoch+1}")
                    break

        self.model.load_state_dict(best_model_wts)
        time_elapsed = time.time() - since
        print(f"Training complete in {time_elapsed // 60:.0f}m {time_elapsed % 60:.0f}s")
        print(f"Best val loss: {best_val_loss:.4f}")

        self._save_model()
        return history
    
    def _save_model(self):
        parts = self.train_path.split('/')
        if 'dog' in parts or 'cat' in parts:
            species = 'dog' if 'dog' in parts else 'cat'
            disease = parts[-1]
            model_save_path = f'./model_trains/{species}_{disease}.pth'
            torch.save(self.model.state_dict(), model_save_path)
            print(f"Best model saved to {model_save_path}")

    def save_accuracy(self, history):
        import matplotlib.pyplot as plt
        plt.plot(history['acc'])
        plt.title('Training Accuracy')
        plt.xlabel('Epoch')
        plt.ylabel('Accuracy')
        plt.savefig(f"{self.train_path.rstrip('/').split('/')[-1]}_acc.png")
        print(f"Accuracy plot saved.")


if __name__ == '__main__':
    train_paths = [
        './alldata/data/Training/label/data/dog/안구/일반/안검내반증',
        './alldata/data/Training/label/data/dog/안구/일반/안검염',
        './alldata/data/Training/label/data/dog/안구/일반/안검종양',
        './alldata/data/Training/label/data/dog/안구/일반/유루증',
        './alldata/data/Training/label/data/dog/안구/일반/핵경화',

        './alldata/data/Training/label/data/cat/안구/일반/각막궤양',
        './alldata/data/Training/label/data/cat/안구/일반/각막부골편',
        './alldata/data/Training/label/data/cat/안구/일반/결막염',
        './alldata/data/Training/label/data/cat/안구/일반/비궤양성각막염',
        './alldata/data/Training/label/data/cat/안구/일반/안검염',
    ]

    for path in train_paths:
        trainer = FineTuning(train_path=path, epoch=20, patience=5, val_split=0.2)
        history = trainer.training()
        trainer.save_accuracy(history)
